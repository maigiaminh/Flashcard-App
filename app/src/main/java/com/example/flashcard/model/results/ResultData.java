package com.example.flashcard.model.results;

public class ResultData {
    private boolean correct;
    private String chosenAnswer;
    private String answer;
    private String question;

    public ResultData(boolean correct, String chosenAnswer, String answer, String question) {
        this.correct = correct;
        this.chosenAnswer = chosenAnswer;
        this.answer = answer;
        this.question = question;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "correct=" + correct +
                ", chosenAnswer='" + chosenAnswer + '\'' +
                ", answer='" + answer + '\'' +
                ", question='" + question + '\'' +
                '}';
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getChosenAnswer() {
        return chosenAnswer;
    }

    public void setChosenAnswer(String chosenAnswer) {
        this.chosenAnswer = chosenAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
