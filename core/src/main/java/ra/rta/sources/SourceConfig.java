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
package ra.rta.sources;

import org.quartz.Job;
import ra.rta.MessageManager;

import java.util.HashMap;
import java.util.Map;

public class SourceConfig {
    public long sourceId = 0;
    public int commandId = 0;
    public String topic;
    public boolean durable = false;
    public String payloadTransformerClass;
    public int batchSize = 1;
    public Class<? extends Job> jobClass;
    public int timeDivision1;
    public int timeDivision2;
    public int timeDivision3;
    public Integer[] timeDivision4;
    public String periodicity;
    public Map<String,Object> props = new HashMap<>();
    public MessageManager messageManager;
}
