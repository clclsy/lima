package nutrisci.util;

import java.util.Map;
import java.util.HashMap;

public class FoodGuideConstants {
    public enum GuideVersion {CFG2007, CFG2019}
    
    private static final Map<GuideVersion, Map<String, Double>> guideMap = new HashMap<>();
    
    static {
        Map<String, Double> cfg2007 = new HashMap<>();
        cfg2007.put("Vegetables & Fruits", 40.0);
        cfg2007.put("Grain Products", 30.0);
        cfg2007.put("Milk & Alternatives", 20.0);
        cfg2007.put("Meat & Alternatives", 10.0);
        guideMap.put(GuideVersion.CFG2007, cfg2007);

        Map<String, Double> cfg2019 = new HashMap<>();
        cfg2019.put("Vegetables & Fruits", 50.0);
        cfg2019.put("Whole Grains", 25.0);
        cfg2019.put("Protein Foods", 25.0);
        guideMap.put(GuideVersion.CFG2019, cfg2019);
    }
    
    public static Map<String, Double> getGuide(GuideVersion version){
        return new HashMap<>(guideMap.get(version));
    }
} 