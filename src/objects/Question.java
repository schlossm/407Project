package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Question.java
 * Alex Rosenberg
 */
public class Question implements Serializable
{
	protected String question;
	protected ArrayList<String> choices = new ArrayList<>();
	protected String correctChoice = "";
	protected double points;

	protected String selectedChoice = "";

	public Question(String question, ArrayList<String> choices, String correctChoice, double points)
	{
		this.question = question;
		this.choices = choices;
		this.correctChoice = correctChoice;
		this.points = points;
	}

	public Question()
	{
		// empty
	}

	public String getQuestion()
	{
		return this.question;
	}

	public void setQuestion(String newQ)
	{
		this.question = newQ;
	}

	public ArrayList<String> getChoices()
	{
		return this.choices;
	}

	public void setChoices(ArrayList<String> newChoices)
	{
		this.choices = newChoices;
	}

	public String getCorrectChoice()
	{
		return this.correctChoice;
	}

	public void setCorrectChoice(String newCorrect)
	{
		this.correctChoice = newCorrect;
	}

	public double getPoints()
	{
		return points;
	}

	public void setPoints(double points)
	{
		this.points = points;
	}

	public String getSelectedChoice()
	{
		return this.selectedChoice;
	}

	public void setSelectedChoice(String newQ)
	{
		this.selectedChoice = newQ;
	}

	public String toString()
	{
		return "question: " + this.question
			       + "\nchoices: " + this.choices
			       + "\ncorrectChoice: " + this.correctChoice
			       + "\npoints: " + this.points;
	}
}
