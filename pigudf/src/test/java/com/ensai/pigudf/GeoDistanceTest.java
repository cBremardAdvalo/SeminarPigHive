package com.ensai.pigudf;

import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.apache.pig.data.TupleFactory;

public class GeoDistanceTest extends TestCase {

    private TupleFactory tf = TupleFactory.getInstance();
    private GeoDistance udf = new GeoDistance();

    public void testExec1() throws Exception {
        Integer dist1 = udf.exec(tf.newTuple(Lists.newArrayList(new Double[]{48.106817, -1.694214, 48.086324, -1.698677})));
        assertEquals(3, dist1.intValue());
    }

    public void testExec2() throws Exception {
        Integer dist1 = udf.exec(tf.newTuple(Lists.newArrayList(new Double[]{48.106817, -1.694214, 48.663552, -1.989654})));
        assertEquals(66, dist1.intValue());
    }


}