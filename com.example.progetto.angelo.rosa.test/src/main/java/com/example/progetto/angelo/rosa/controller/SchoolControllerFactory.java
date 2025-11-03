package com.example.progetto.angelo.rosa.controller;

import com.example.progetto.angelo.rosa.view.StudentView;

public interface SchoolControllerFactory {
	SchoolController create(StudentView view);
}