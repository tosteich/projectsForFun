package ua.step.weather.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import ua.step.weather.beans.Day;

public class AverageTag extends SimpleTagSupport {
	private String value;
	private List<Day> days;

	public void doTag() throws JspException, IOException {

		if (days != null) {
			Double temp = days.stream().mapToInt(x -> x.getTemp()).average().orElse(0) / 100;
			getJspContext().setAttribute(value, String.format("%.2f", temp));
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
	}

}
