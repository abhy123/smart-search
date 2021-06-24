package com.smart.search;

import com.smart.search.model.Searchable;

public class Players extends Searchable {

    String name;

    public Players(String name) {
        super(null, name);
        this.name = name;
    }

}
