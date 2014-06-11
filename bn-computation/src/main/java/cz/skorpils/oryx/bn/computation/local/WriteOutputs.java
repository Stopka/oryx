package cz.skorpils.oryx.bn.computation.local;

import cz.skorpils.oryx.bn.common.model.BayesNetwork;
import cz.skorpils.oryx.bn.common.pmml.BNModelDescription;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Created by stopka on 10.6.14.
 */
public class WriteOutputs  implements Callable<Object> {
    private static final String SINGLE_OUT_FILENAME = "0.obj";
    File dir;
    BayesNetwork model;
    public WriteOutputs(File dir, BayesNetwork model) {
        this.dir=dir;
        this.model=model;
    }

    @Override
    public Object call() throws Exception {
        writeNetwork(model, new File(dir, "network"));
        File modelFile = new File(dir, "model.pmml.gz");
        BNModelDescription modelDescription = new BNModelDescription();
        modelDescription.setNetworkPath("network");
        BNModelDescription.write(modelFile, modelDescription);
        return null;
    }

    private static void writeNetwork(BayesNetwork network, File networkDir) throws IOException {
        File outFile = new File(networkDir, SINGLE_OUT_FILENAME);
        FileOutputStream fos = new FileOutputStream(outFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        try {
            oos.writeObject(network);
        } finally {
            oos.close();
            fos.close();
        }
    }
}
