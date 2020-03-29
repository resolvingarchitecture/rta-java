package ra.rta.rfm.conspref.bolts;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import java.io.File;
import java.util.List;

/**
 *
 */
public class RuleRunner {
    public RuleRunner()
    {
    }

    public void runRules(String folderPath, List<Object> facts) throws Exception
    {
        KieServices services = KieServices.Factory.get();
        KieFileSystem fileSystem = services.newKieFileSystem();
        File folder = new File(folderPath);
        File[] drlFiles = folder.listFiles();
        Resource resource;
        for(File drlFile : drlFiles) {
            resource = services.getResources().newFileSystemResource(drlFile).setResourceType(ResourceType.DRL);
            fileSystem.write(resource);
        }
        KieBuilder builder = services.newKieBuilder(fileSystem);
        builder.buildAll();

        if (builder.getResults().hasMessages(Message.Level.ERROR))
        {
            throw new RuntimeException("Build Errors:\n" + builder.getResults().toString());
        }

        KieContainer kContainer = services.newKieContainer(services.getRepository().getDefaultReleaseId());

        StatelessKieSession session = kContainer.newStatelessKieSession();

        session.execute(facts);
    }
}
