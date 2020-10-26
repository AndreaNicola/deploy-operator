package it.makeit.operator.crd.app;

import com.google.gson.Gson;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppWatcher {


    public static void init() throws IOException {

        KubernetesClient client = new DefaultKubernetesClient();
        client.customResource(AppCdrUtils.createAppContext()).watch(new Watcher<>() {

            @Override
            public void eventReceived(Action action, String s) {
                Gson gson = new Gson();
                AppCdr app = gson.fromJson(s, AppCdr.class);

                switch (action) {
                    case ADDED:
                    case MODIFIED:
                        log.debug("Created/Updated App CDR {}/{}", app.getMetadata().getNamespace(), app.getMetadata().getName());
                        app.createDeployment();
                        app.createService();
                        app.createIngress();
                        break;
                    case ERROR:
                        break;
                    case DELETED:
                        log.debug("Deleted App CDR {}/{}", app.getMetadata().getNamespace(), app.getMetadata().getName());
                        app.deleteDeployment();
                        app.deleteService();
                        app.deleteIngress();
                        break;
                }

            }


            @Override
            public void onClose(KubernetesClientException e) {


            }

        });

    }


}
