package it.makeit.operator.crd.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AppSpec {

    private String imageName;
    private boolean ingressAcme;
    private String ingressHostName;
    private String ingressPath;
    private IngressPathType ingressPathType;
    private String ingressRewritePath;
    private boolean ingressTls;
    private List<AppParameter> parameters;
    private Integer port;
    private Integer replicas;

}
