package test;

import com.bellszhu.elasticsearch.plugin.DynamicSynonymPlugin;
import com.bellszhu.elasticsearch.plugin.synonym.analysis.DBSynonymFile;

/**
 * Created by liugexiang on 2019/2/22.
 */
public class PluginTest   {
    public static void main(String[] args) {

        DynamicSynonymPlugin plugin=new DynamicSynonymPlugin();
        plugin.getTokenFilters();
        DBSynonymFile dbSynonymFile=new DBSynonymFile("",false);
        dbSynonymFile.reloadSynonymMap();
    }
}
