package ua.step.weather.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import ua.step.weather.beans.Day;

@Component
@SessionScope
public class DaysController implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<Day> days = new ArrayList<>();
	private List<Day> selectedDays = new ArrayList<>();
	private List<Date> months;
	private String selection;

	public DaysController() {
		setDays(initDays());
		setSelection("0-0");
	}

	public List<Day> getDays() {
		return days;
	}

	public void setDays(List<Day> days) {
		this.days = days;
		setMonths();
	}

	public List<Day> getSelectedDays() {
		return selectedDays;
	}

	public void setSelectedDays() {
		selectedDays = this.days;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
		String[] sel = selection.split("-");
		if (sel.length == 2) {
			try {
				int month = Integer.parseInt(sel[0]);
				int year = Integer.parseInt(sel[1]);
				if ((month > 0 && month <= 12)) {
					List<Day> temp = days.stream()
							.filter(x -> x.getMonth() == month && x.getYear() == year)
							.collect(Collectors.toList());
					selectedDays = temp.size() == 0? days: temp;
					return;
				} 
			} catch (NumberFormatException nfe) {
			}
		}
		selectedDays = days;
	}

	public void setMonths() {
		HashSet<String> seen = new HashSet<>();
		List<Day> temp = new ArrayList<>(days);
		temp.removeIf(e -> !seen.add(e.getMonthAndYear()));
		months = temp.stream()
				.map(x -> x.toDate())
				.collect(Collectors.toList());
	}

	public List<Date> getMonths() {
		return months;
	}
	
	private List<Day> initDays () {
		List<Day> days = new ArrayList<>();
		String fileName = "report.csv";
		InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		if (ins != null) {
		BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            String str;
            try {
				while ((str = br.readLine()) != null)
				{
					int [] values = Arrays.stream(str.split(";|\\."))
							.limit(4)
							.mapToInt(Integer::valueOf)
							.toArray();
				    days.add(new Day(values));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return days;
	}
}

