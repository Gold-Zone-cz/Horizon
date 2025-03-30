package cz.goldzone.horizon.enums;


import com.cryptomorin.xseries.XMaterial;
import cz.goldzone.horizon.managers.PlayerWarpsManager;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Objects;

@Getter
public enum Category {
    FARMS("<yellow>FARMS", 21, XMaterial.BLAZE_ROD),
    SHOPS("<green>SHOPS", 23, XMaterial.EMERALD),
    MISC("<aqua>MISC", 25, XMaterial.BOOK);

    private final String displayName;
    private final int slot;
    private final Material material;

    Category(String displayName, int slot, XMaterial xMaterial) {
        this.displayName = displayName;
        this.slot = slot;
        this.material = Objects.requireNonNull(xMaterial.parseMaterial(), "Material not found: " + xMaterial.name());
    }

    public String getPopularPlayerWarp() {
        return PlayerWarpsManager.getMostVisitedWarp();
    }
}
