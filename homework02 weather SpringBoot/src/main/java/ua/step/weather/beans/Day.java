package ua.step.weather.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Day implements Serializable {
	private static final long serialVersionUID = 1L;
	private int year;
	private int month;
	private int dayOfMonth;
	private int temp;

	public Day() {

	}

	public Day(int... values) {
		if (values.length == 4) {
			this.year = values[0];
			this.month = values[1];
			this.dayOfMonth = values[2];
			this.temp = values[3];
		}
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public int getTemp() {
		return temp;
	}

	public void setTemp(int temp) {
		this.temp = temp;
	}

	public String getMonthAndYear() {
		StringBuilder sb = new StringBuilder(String.valueOf(month));
		return sb.append(String.valueOf(year)).toString();
	}

	public Date toDate() {
		LocalDate day = LocalDate.of(this.getYear(), this.getMonth(), this.getDayOfMonth());
		return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

}
