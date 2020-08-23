package ua.step.weather.tags;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import ua.step.weather.beans.Day;

public class WarmestDaysTag extends SimpleTagSupport {
	private String value;
	private List<Day> days;

	public void doTag() throws JspException, IOException {

		if (days != null) {
			List<Day> temp = days.stream().sorted(Comparator.comparing(Day::getTemp).reversed()).limit(3)
					.collect(Collectors.toList());
			getJspContext().setAttribute(value, temp);
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
