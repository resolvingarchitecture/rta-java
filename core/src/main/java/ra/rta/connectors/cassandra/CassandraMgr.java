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
package ra.rta.connectors.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CassandraMgr {

    private static Logger LOG = LoggerFactory.getLogger(CassandraMgr.class);

    private String keyspace;
    private String seedNode;
    private Session session;
    private Cluster cluster;
    private String repoClassesStr;
    private boolean initialized = false;

    private static CassandraMgr instance = null;
    private static final Object lock = new Object();

    private CassandraMgr(){}

    public static CassandraMgr init(Map map) {
        synchronized (lock) {
            if (instance == null) {
                instance = new CassandraMgr();
                instance.keyspace = (String) map.get("topology.keyspace");
                instance.seedNode = (String) map.get("topology.cassandra.seednode");
                instance.cluster = Cluster.builder().addContactPoint(instance.seedNode).build();
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.LOCAL, 12);
//			cluster.getConfiguration().getPoolingOptions().setCoreConnectionsPerHost(HostDistance.REMOTE, 12);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.LOCAL, 16);
//			cluster.getConfiguration().getPoolingOptions().setMaxConnectionsPerHost(HostDistance.REMOTE, 16);
                instance.session = instance.cluster.connect(instance.keyspace);
            }
        }
        return instance;
    }

    public static CassandraMgr getInstance() {
        return instance;
    }

    public Session getSession() {
        return session;
    }

}
