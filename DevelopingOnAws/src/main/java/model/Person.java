package model;

import java.time.LocalDate;

public class Person {
	private String name;
	private int age;
	private LocalDate dateOfBirth;
	
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
		this.dateOfBirth = null;
	}
	
	public Person(String name, int age, LocalDate dateOfBirth) {
		this.name = name;
		this.age = age;
		this.dateOfBirth = dateOfBirth;
	}
	
	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}
			
	public LocalDate getDateOfBirth() {		
		return dateOfBirth;
	}

	public String toString() {			
		return "name: " + this.name + "\n" + "age: " + this.age + "\n" + "dob: " + this.dateOfBirth.toString();
	}
}