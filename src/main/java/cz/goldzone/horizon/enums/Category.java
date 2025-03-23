package cz.goldzone.horizon.enums;


import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Objects;

@Getter
public enum Category {
    FARM("§FARM", 21, XMaterial.BLAZE_ROD),
    SHOP("§SHOPS", 23, XMaterial.EMERALD),
    MISC("§bMISC", 25, XMaterial.BOOK);

    private final String displayName;
    private final int slot;
    private final Material material;

    Category(String displayName, int slot, XMaterial xMaterial) {
        this.displayName = displayName;
        this.slot = slot;
        this.material = Objects.requireNonNull(xMaterial.get(), "Invalid material for " + name());
    }

}
