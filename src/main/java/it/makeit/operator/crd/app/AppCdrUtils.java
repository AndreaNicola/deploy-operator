package it.makeit.operator.crd.app;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBuilder;
import io.fabric8.kubernetes.api.model.extensions.IngressRuleBuilder;
import io.fabric8.kubernetes.api.model.extensions.IngressTLSBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
class AppCdrUtils {

    static void createDeployment(AppCdr appCdr) {

        Function<AppParameter, EnvVar> createEnvVar = appParameter -> {
            EnvVar result = new EnvVar();
            result.setName(appParameter.getName());
            result.setValue(appParameter.getValue());
            return result;
        };

        List<EnvVar> envVars = appCdr.getSpec().getParameters().stream().map(createEnvVar).collect(Collectors.toList());

        Deployment deployment = new DeploymentBuilder()
                .withNewMetadata()
                .withName(appCdr.getMetadata().getName())
                .withNamespace(appCdr.getMetadata().getNamespace())
                .addToLabels("app", appCdr.getMetadata().getName())
                .endMetadata()
                .withNewSpec()
                .withReplicas(appCdr.getSpec().getReplicas())
                .withNewSelector()
                .addToMatchLabels("app", appCdr.getMetadata().getName())
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .addToLabels("app", appCdr.getMetadata().getName())
                .addToLabels("deploy-operator-app", appCdr.getMetadata().getName())
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(appCdr.getMetadata().getName())
                .withImage(appCdr.getSpec().getImageName())
                .addNewPort()
                .withContainerPort(appCdr.getSpec().getPort())
                .endPort()
                .withEnv(envVars)
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            deployment = client.apps().deployments()
                    .inNamespace(deployment.getMetadata().getNamespace())
                    .createOrReplace(deployment);

            log.debug("Created/Updated deployment {}/{}", deployment.getMetadata().getNamespace(), deployment.getMetadata().getName());
        }

    }

    static void createService(AppCdr appCdr) {

        Map<String, String> selector = new HashMap<>();
        selector.put("app", appCdr.getMetadata().getName());

        Service service = new ServiceBuilder()
                .withNewMetadata()
                .withName(appCdr.getMetadata().getName())
                .withNamespace(appCdr.getMetadata().getNamespace())
                .endMetadata()
                .withNewSpec()
                .withSelector(selector)
                .addNewPort()
                .withName(appCdr.getMetadata().getName())
                .withProtocol("TCP")
                .withPort(appCdr.getSpec().getPort())
                .endPort()
                .withNewType("ClusterIP")
                .endSpec()
                .build();

        try (KubernetesClient client = new DefaultKubernetesClient()) {
            service = client.services()
                    .inNamespace(service.getMetadata().getNamespace())
                    .createOrReplace(service);

            log.debug("Created/Updated service {}/{}", service.getMetadata().getNamespace(), service.getMetadata().getName());

        }

    }

    static void deleteIngress(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            Ingress ingress = appCdr.getIngress();
            if (ingress != null) {
                client.extensions().ingresses().inNamespace(ingress.getMetadata().getNamespace()).delete(ingress);
                log.debug("Deleted ingress {}/{}", ingress.getMetadata().getNamespace(), ingress.getMetadata().getName());

            }
        }
    }

    static Ingress getIngress(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            return client.extensions().ingresses()
                    .inNamespace(appCdr.getMetadata().getNamespace())
                    .withName(appCdr.getMetadata().getNamespace())
                    .get();

        }
    }

    static void deleteService(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            Service service = appCdr.getService();
            if (service != null) {
                client.services().inNamespace(service.getMetadata().getNamespace()).delete(service);
                log.debug("Deleted service {}/{}", service.getMetadata().getNamespace(), service.getMetadata().getName());
            }
        }
    }

    static Service getService(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            return client.services()
                    .inNamespace(appCdr.getMetadata().getNamespace())
                    .withName(appCdr.getMetadata().getName())
                    .get();
        }
    }

    static void deleteDeployment(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            Deployment deployment = appCdr.getDeployment();
            if (deployment != null) {
                client.apps().deployments().inNamespace(deployment.getMetadata().getNamespace()).delete(deployment);
                log.debug("Deleted deployment {}/{}", deployment.getMetadata().getNamespace(), deployment.getMetadata().getName());
            }
        }
    }

    static Deployment getDeployment(AppCdr appCdr) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            return client.apps().deployments().inNamespace(appCdr.getMetadata().getNamespace()).withName(appCdr.getMetadata().getName()).get();
        }
    }

    static void
    createIngress(AppCdr appCdr) {

        try (KubernetesClient client = new DefaultKubernetesClient()) {

            Map<String, String> annotations = new HashMap<>();
            annotations.put("kubernetes.io/ingress.class", "nginx");

            if (!StringUtils.isEmpty(appCdr.getSpec().getIngressRewritePath())) {
                annotations.put("nginx.ingress.kubernetes.io/rewrite-target", appCdr.getSpec().getIngressRewritePath());
            }

            if (appCdr.getSpec().isIngressAcme()) {
                //TODO cluster-issuer!
            }

            Ingress ingress = new IngressBuilder()
                    .withNewMetadata()
                    .withName(appCdr.getMetadata().getName())
                    .withNamespace(appCdr.getMetadata().getNamespace())
                    .withAnnotations(annotations)
                    .endMetadata()
                    .withNewSpec()
                    .withRules(new IngressRuleBuilder()
                            .withNewHost(appCdr.getSpec().getIngressHostName())
                            .withNewHttp()
                            .addNewPath()
                            .withNewPath(appCdr.getSpec().getIngressPath() == null ? "/" : appCdr.getSpec().getIngressPath())
                            .withNewPathType(appCdr.getSpec().getIngressPath() == null ? IngressPathType.ImplementationSpecific.toString() : IngressPathType.Prefix.toString())
                            .withNewBackend()
                            .withServiceName(appCdr.getMetadata().getName())
                            .withNewServicePort(appCdr.getSpec().getPort())
                            .endBackend()
                            .endPath()
                            .endHttp()
                            .build())


                    .withTls(new IngressTLSBuilder()
                            .withHosts(appCdr.getSpec().getIngressHostName())
                            .withSecretName(appCdr.getMetadata().getName())
                            .build())

                    .endSpec().build();


            client.extensions().ingresses()
                    .inNamespace(ingress.getMetadata().getNamespace())
                    .createOrReplace(ingress);

            log.debug("Created/Updated ingress {}/{}", ingress.getMetadata().getNamespace(), ingress.getMetadata().getName());


        }
    }

    static CustomResourceDefinitionContext createAppContext() {
        return new CustomResourceDefinitionContext
                .Builder()
                .withGroup("makeit.it")
                .withVersion("v1")
                .withScope("Namespaced")
                .withPlural("apps")
                .build();
    }

}
