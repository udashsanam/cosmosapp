package com.cosmos.astrologer;

import com.cosmos.astrologer.dto.PreviouslyAskedQuestions;
import com.cosmos.astrologer.dto.QuestionerDetails;
import com.cosmos.astrologer.dto.QuestionerDetailsResponse;
import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.questionPool.entity.NepaliQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/astrologer")
public class AstrologerController {

    private NepaliQuestionPoolRepo nepQsnRepo;
    private UserRepository userRepo;
    private NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    private EnglishAnswerPoolRepo englishAnswerPoolRepo;

    @Autowired
    public AstrologerController(NepaliQuestionPoolRepo nepQsnRepo, UserRepository userRepo, NepaliAnswerPoolRepo nepaliAnswerPoolRepo,
                                EnglishAnswerPoolRepo englishAnswerPoolRepo) {
        this.nepQsnRepo = nepQsnRepo;
        this.userRepo = userRepo;
        this.nepaliAnswerPoolRepo = nepaliAnswerPoolRepo;
        this.englishAnswerPoolRepo = englishAnswerPoolRepo;
    }

    /*Fetch translated unassigned question*/
    @GetMapping("/ua-questions/fetch")
    public QuestionerDetailsResponse getUnAssignedQuestion() {

        // 1. first find astrologer unfinished question. (search for nepali question that has
        // astrologer id of this current user id and status -> assigned
        // if not then fetch unassigned question from pool
        // mark status of fetched unassigned question as assigned and provide this astrologer id to this question.

        QuestionerDetailsResponse qdr = new QuestionerDetailsResponse();
        QuestionerDetails questionerDetails = new QuestionerDetails();
        List<PreviouslyAskedQuestions> previouslyAskedQuestions = new ArrayList<>();

        /*Check if Astrologer has left uncompleted task before*/
        NepaliQuestionPool prevTask = nepQsnRepo.findAllUncompletedTaskByAstroId(getCurrentUserId());
        if (prevTask != null) {
            questionerDetails.setQuestion(prevTask.getNepQuestion());
            questionerDetails.setQuestionId(prevTask.getNepQuesId());

            /*Who asked this question*/
            User user = userRepo.findByUserId(prevTask.getUserId());
            setResponseObject(qdr, questionerDetails, previouslyAskedQuestions, user);
        } else {
            /*Get first unassigned question*/
            NepaliQuestionPool question = nepQsnRepo.findUnAssignedQuestion();
            if (question != null) {
                /*Initially set astroId and status as assigned and store the information*/
                question.setQuestionStatus(QuestionStatus.Assigned);
                question.setAssignedAstroId(getCurrentUserId());
                nepQsnRepo.save(question);

                questionerDetails.setQuestion(question.getNepQuestion());
                questionerDetails.setQuestionId(question.getNepQuesId());

                /*Who asked this question*/
                User user = userRepo.findByUserId(question.getUserId());
                setResponseObject(qdr, questionerDetails, previouslyAskedQuestions, user);
            } else {
                throw new CustomException("No task in system", HttpStatus.NOT_FOUND);
            }
        }
        return qdr;
    }

    private void setResponseObject(QuestionerDetailsResponse qdr, QuestionerDetails questionerDetails, List<PreviouslyAskedQuestions> previouslyAskedQuestions, User user) {
        if (user != null) {
            questionerDetails.setUser(user);
            /*This user may have created several accounts to check the astrologer*/
            List<User> possibleDuplicateUsers = userRepo.findAllByDateOfBirthAndBirthTime(user.getDateOfBirth(), user.getBirthTime());

            if (!possibleDuplicateUsers.isEmpty()) {
                /*Get all questions asked previously by possible duplicate users*/
                possibleDuplicateUsers.forEach(user1 -> {
                        englishAnswerPoolRepo.getHistoryAnswerByAstromode(user1.getUserId()).stream().forEach(previouslyAskedQuestions1 -> {
                            PreviouslyAskedQuestions question1 = new PreviouslyAskedQuestions();
                            question1.setAnswer(previouslyAskedQuestions1.getAnswer());
                            question1.setRole(previouslyAskedQuestions1.getRole());
                            question1.setQuestionId(previouslyAskedQuestions1.getQuestionId());
                            question1.setQuestion(previouslyAskedQuestions1.getQuestion());
                            question1.setUserId(previouslyAskedQuestions1.getUserId());
                            previouslyAskedQuestions.add(question1);
                        });
                        nepQsnRepo.findAllByUserId(user1.getUserId())
                        .stream()
                        .filter(qsn -> qsn.getNepQuestion() != null)
                        .forEach(qsn -> {
                            PreviouslyAskedQuestions question1 = new PreviouslyAskedQuestions();
                            question1.setUserId(user1.getUserId());
                            question1.setQuestionId(qsn.getNepQuesId());
                            question1.setQuestion(qsn.getNepQuestion());

                            /*fetch answer of this question if present*/
                            NepaliAnswerPool answer = nepaliAnswerPoolRepo.getOneByNepQuestionIdAndUserId(qsn.getNepQuesId(), qsn.getUserId());
                            if (answer != null) {
                                question1.setAnswer(answer.getAnswer());
                            } else question1.setAnswer("N/A");

                            previouslyAskedQuestions.add(question1);
                        }); });
                questionerDetails.setPreviousQueries(previouslyAskedQuestions);

                /*Remove the user itself who have asked the question and set remaining users as possible duplicate users*/
                possibleDuplicateUsers.removeIf(user1 -> user1.getUserId().equals(user.getUserId()));
                questionerDetails.setPossibleDuplicateUsers(possibleDuplicateUsers);
            }
            qdr.setSuccess(true);
            qdr.setQuestionerDetails(questionerDetails);
        }
    }

    /*submit answer to user's questions*/
    @PostMapping("/ua-questions/answer")
    @ResponseBody
    public NepaliAnswerPool submitAstrologersAnswer(@RequestBody NepaliAnswerPool replyBody) {
        if (replyBody.getAnswer().equalsIgnoreCase("") || replyBody.getAnswer() == null) {
            throw new CustomException("Answer field is mandatory!", HttpStatus.NOT_FOUND);
        } else {
            /*Set question as assigned along with assigned astrologer Id*/
            NepaliQuestionPool nepQsn = nepQsnRepo.findById(replyBody.getNepQuestionId())
                    .orElseThrow(() -> new CustomException("No question found.", HttpStatus.NOT_FOUND));
            nepQsn.setAssignedAstroId(getCurrentUserId());
            nepQsn.setQuestionStatus(QuestionStatus.Clear);
            nepQsnRepo.save(nepQsn);

            /*Store the question's answer*/
            /*Check if question has already been answered*/
            NepaliAnswerPool replyFromAstrologer = nepaliAnswerPoolRepo.getOneByNepQuestionIdAndUserId(replyBody.getNepQuestionId(), replyBody.getUserId());
            if (replyFromAstrologer != null) {
                throw new CustomException("Question has already been answered.", HttpStatus.CONFLICT);
            } else {
                NepaliAnswerPool reply = new NepaliAnswerPool();
                reply.setNepQuestionId(replyBody.getNepQuestionId());
                reply.setUserId(replyBody.getUserId());
                reply.setAnswer(replyBody.getAnswer());
                reply.setStatus(QuestionStatus.UnAssigned);

                return nepaliAnswerPoolRepo.save(reply);
            }
        }
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }


}
