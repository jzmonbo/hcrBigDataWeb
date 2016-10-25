package com.hcrcms.cms.entity.assist.base;

import java.io.Serializable;

public class BaseJiYinDaShuJu implements Serializable{

	private String team;				// 团队名称
	private String score;				// 分数
	private Integer ranking;				// 排名
	private Integer lastRanking;		// 上次排名
	private String lastSubmissionDate;	// 最后提交时间
	private String bestSubmissionDate;	// 最佳提交时间
	private String entries;				// 提交次数
	
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public Integer getRanking() {
		return ranking;
	}
	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}
	public Integer getLastRanking() {
		return lastRanking;
	}
	public void setLastRanking(Integer lastRanking) {
		this.lastRanking = lastRanking;
	}
	public String getLastSubmissionDate() {
		return lastSubmissionDate;
	}
	public void setLastSubmissionDate(String lastSubmissionDate) {
		lastSubmissionDate = lastSubmissionDate.replaceFirst("T"," ");
		lastSubmissionDate = lastSubmissionDate.substring(0,lastSubmissionDate.lastIndexOf("."));
		this.lastSubmissionDate = lastSubmissionDate;
	}
	public String getBestSubmissionDate() {
		return bestSubmissionDate;
	}
	public void setBestSubmissionDate(String bestSubmissionDate) {
		bestSubmissionDate = bestSubmissionDate.replaceFirst("T"," ");
		bestSubmissionDate = bestSubmissionDate.substring(0,bestSubmissionDate.lastIndexOf("."));
		this.bestSubmissionDate = bestSubmissionDate;
	}
	public String getEntries() {
		return entries;
	}
	public void setEntries(String entries) {
		this.entries = entries;
	}
}
