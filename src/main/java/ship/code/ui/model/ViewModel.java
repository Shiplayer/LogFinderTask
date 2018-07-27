package ship.code.ui.model;

import ship.code.utils.LiveData;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModel {
    private final static Map<JFrame, ViewModel> jFrameList = new HashMap<>();
    private List<LiveData> liveDataList;

    public static ViewModel of(JFrame jframe){
        jFrameList.put(jframe, new ViewModel());
        jframe.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                for (ViewModel viewModel : jFrameList.values()){
                    viewModel.close();
                }
            }
        });
        return this;
    }

    private void close(){
        for(LiveData liveData : liveDataList){
            liveData.removeObservers();
        }
    }
}
