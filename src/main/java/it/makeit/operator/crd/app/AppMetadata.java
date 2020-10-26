package it.makeit.operator.crd.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class AppMetadata {

    private Map<String, String> annotations;
    private Map<String, String> labels;
    private String name;
    private String namespace;

}
