/*
  This is free and unencumbered software released into the public domain.

  Anyone is free to copy, modify, publish, use, compile, sell, or
  distribute this software, either in source code form or as a compiled
  binary, for any purpose, commercial or non-commercial, and by any
  means.

  In jurisdictions that recognize copyright laws, the author or authors
  of this software dedicate any and all copyright interest in the
  software to the public domain. We make this dedication for the benefit
  of the public at large and to the detriment of our heirs and
  successors. We intend this dedication to be an overt act of
  relinquishment in perpetuity of all present and future rights to this
  software under copyright law.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.

  For more information, please refer to <http://unlicense.org/>
 */
package ra.rta.rfm.conspref.publish.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;
import org.springframework.data.repository.CrudRepository;

import java.util.HashMap;
import java.util.Map;

public class CassandraRepoMgr {

    private static Logger LOG = LoggerFactory.getLogger(CassandraRepoMgr.class);

    private String keyspace;
    private String seedNode;
    private Session session;
    private Cluster cluster;
    private String repoClassesStr;
    private Map<String, CrudRepository> repos = new HashMap<>();
    private boolean initialized = false;

    private static CassandraRepoMgr instance = null;

    public static void init(Map map) {
        instance = new CassandraRepoMgr();
        instance.keyspace = (String)map.get("topology.keyspace");
        instance.seedNode = (String)map.get("topology.cassandra.seednode");
        instance.cluster = Cluster.builder().addContactPoint(instance.seedNode).build();
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.LOCAL, 12);
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.REMOTE, 12);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.LOCAL, 16);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.REMOTE, 16);
        instance.session = instance.cluster.connect(instance.keyspace);
        CassandraTemplate template = new CassandraTemplate(instance.session);
        CassandraRepositoryFactory repoFactory = new CassandraRepositoryFactory(template);

        instance.repoClassesStr = (String)map.get("topology.repositories");
        String[] repoClasses = instance.repoClassesStr.split(",");
        for(String repoClass : repoClasses) {
            try {
                instance.repos.put(repoClass,(CrudRepository) repoFactory.getRepository(Class.forName(repoClass)));
            } catch (ClassNotFoundException e) {
                LOG.warn(e.getLocalizedMessage());
            }
        }
    }

    public static CassandraRepoMgr getInstance() {
        return instance;
    }

}
