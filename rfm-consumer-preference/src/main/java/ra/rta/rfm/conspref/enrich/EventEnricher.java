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
package ra.rta.rfm.conspref.enrich;

import ra.rta.enrich.Enrichable;
import ra.rta.models.Enricher;
import ra.rta.Event;
import ra.rta.rfm.conspref.models.Identity;
import ra.rta.connectors.PersistenceManager;

import java.util.Calendar;

public class EventEnricher implements Enricher {

    @Override
    public void enrich(Enrichable enrichable) throws Exception {
        if(enrichable instanceof Event) {
            Event event = (Event)enrichable;
            if(event.indId > 0) {
                event.identity = (Identity) PersistenceManager.loadIndividual(event.indId);
            }
        }
        if(individual==null) {
            LOG.warn("No Individual found in Enrichable; unable to enrich.");
            return;
        }
        if (individual.birthYear > 1900 && (individual.age < 0 || individual.age > 200)) {
            individual.age = Calendar.getInstance().get(Calendar.YEAR) - individual.birthYear;
            individual.save = true;
        }
    }
}
