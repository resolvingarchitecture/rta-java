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
package ra.rta.publish;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ra.rta.BaseEventEmitterBolt;
import ra.rta.models.Event;
import ra.rta.utilities.JSONUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class FilePublishBolt extends BaseEventEmitterBolt {

    private static final Logger LOG = LoggerFactory.getLogger(FilePublishBolt.class);

    private File publishDir;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
        String dirPath = (String)map.get("topology.publish.file.dir");
        if(dirPath==null) {
            LOG.warn("Unable to prepare FilePublishBolt. Please set directory in property: topology.publish.file.dir");
            return;
        }
        publishDir = new File(dirPath);
        if(!publishDir.exists() && !publishDir.mkdir()) {
            LOG.warn("Unable to publish to directory; creation failed: "+dirPath);
        }
    }

    @Override
    public void execute(Event event) throws Exception {
        if(publishDir!=null) {
            File eventFile = new File(publishDir, event.id+"");
            if(eventFile.createNewFile()) {
                Files.write(Paths.get(eventFile.toURI()), JSONUtil.MAPPER.writeValueAsBytes(event));
            } else {
                LOG.warn("Unable to create new event file: "+eventFile.getAbsolutePath());
            }
        } else {
            LOG.warn("Unable to persist event (id="+event.id+"): directory doesn't exist. Please set with property: topology.publish.file.dir");
        }
    }
}
