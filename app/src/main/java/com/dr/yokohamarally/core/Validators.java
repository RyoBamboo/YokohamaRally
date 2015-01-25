package com.dr.yokohamarally.core;

import java.util.ArrayList;
import java.util.List;

public class Validators {
    private List<Validatable> validateLists;

    public Validators() {
        validateLists = new ArrayList<Validatable>();
    }

    public void add(Validatable validatable) {
        validateLists.add(validatable);
    }

    public List<Validatable> as_list() {
        return validateLists;
    }
}
