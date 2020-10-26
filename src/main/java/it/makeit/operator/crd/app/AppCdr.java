package it.makeit.operator.crd.app;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Slf4j
public class AppCdr {

    private String apiVersion;
    private AppMetadata metadata;
    private AppSpec spec;

    public void createDeployment() {
        AppCdrUtils.createDeployment(this);
    }

    public void createService() {
        AppCdrUtils.createService(this);
    }

    public void deleteIngress() {
        AppCdrUtils.deleteIngress(this);
    }

    public Ingress getIngress() {
        return AppCdrUtils.getIngress(this);
    }

    public void deleteService() {
        AppCdrUtils.deleteService(this);
    }

    public Service getService() {
        return AppCdrUtils.getService(this);
    }

    public void deleteDeployment() {
        AppCdrUtils.deleteDeployment(this);
    }

    public Deployment getDeployment() {
        return AppCdrUtils.getDeployment(this);
    }

    public void createIngress() {
        AppCdrUtils.createIngress(this);
    }

}
