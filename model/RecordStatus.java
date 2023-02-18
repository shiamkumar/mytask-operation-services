package com.ghx.api.operations.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordStatus implements Serializable {

    private static final long serialVersionUID = 7048018695233968165L;

    private String id;
    private String status;
}
