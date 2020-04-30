package de.fs.fintech.geogame.data;

/**
 * Created by axel on 20.05.17.
 */

public class InventoryType {

    public static enum Category {
        Key,
        Media,
        Resonators,
        PowerCube,
        Weapon,
        Mod
    }

    public static Category getKind(Kind type) {
        int o=type.ordinal();
        if(o<=Kind.KEY.ordinal()) return Category.Key;
        if(o<=Kind.Media.ordinal()) return Category.Media;
        if(o<=Kind.KEY.ordinal()) return Category.Resonators;
        if(o<=Kind.KEY.ordinal()) return Category.PowerCube;
        if(o<=Kind.KEY.ordinal()) return Category.Weapon;
        if(o<=Kind.KEY.ordinal()) return Category.Mod;

        return null;
    }

    public static enum Kind {
        KEY,
        Media,
        RESO,
        PowerCube,
        Recharger,
        XMP,
        UltraStrike,
        Virus,
        MultiHack,
        HeatSink,
        LinkAmp,
        Shield,
        Turret,
        ForceAmp,
        Firewall,
        AntiVirus,
        Cloaking
    }

    public static Kind getKind(Type type) {
        int o=type.ordinal();
        if(o<=Type.KEY.ordinal()) return Kind.KEY;
        if(o<=Type.Media.ordinal()) return Kind.Media;
        if(o<=Type.KEY.ordinal()) return Kind.RESO;
        if(o<=Type.KEY.ordinal()) return Kind.PowerCube;
        if(o<=Type.KEY.ordinal()) return Kind.Recharger;
        if(o<=Type.KEY.ordinal()) return Kind.XMP;
        if(o<=Type.KEY.ordinal()) return Kind.UltraStrike;
        if(o<=Type.KEY.ordinal()) return Kind.Virus;
        if(o<=Type.KEY.ordinal()) return Kind.MultiHack;
        if(o<=Type.KEY.ordinal()) return Kind.HeatSink;
        if(o<=Type.KEY.ordinal()) return Kind.LinkAmp;
        if(o<=Type.KEY.ordinal()) return Kind.Shield;
        if(o<=Type.KEY.ordinal()) return Kind.Turret;
        if(o<=Type.KEY.ordinal()) return Kind.ForceAmp;
        if(o<=Type.KEY.ordinal()) return Kind.Firewall;
        if(o<=Type.KEY.ordinal()) return Kind.AntiVirus;
        if(o<=Type.KEY.ordinal()) return Kind.Cloaking;

        return null;
    }

    public static enum Type {
        KEY,
        Media,
        RESO_L1,
        RESO_L2,
        RESO_L3,
        RESO_L4,
        RESO_L5,
        RESO_L6,
        RESO_L7,
        RESO_L8,
        XMP_L1,
        XMP_L2,
        XMP_L3,
        XMP_L4,
        XMP_L5,
        XMP_L6,
        XMP_L7,
        XMP_L8,
        UltraStrike_L1,
        UltraStrike_L2,
        UltraStrike_L3,
        UltraStrike_L4,
        UltraStrike_L5,
        UltraStrike_L6,
        UltraStrike_L7,
        UltraStrike_L8,
        Virus_A,
        Virus_B,
        Virus_C,
        Virus_D,
        MultiHack_Common,
        MultiHack_Rare,
        MultiHack_VeryRare,
        MultiHack_ExtremelyRare,
        HeatSink_Common,
        HeatSink_Rare,
        HeatSink_VeryRare,
        HeatSink_ExtremelyRare,
        LinkAmp_Common,
        LinkAmp_Rare,
        LinkAmp_VeryRare,
        LinkAmp_ExtremelyRare,
        Shield_Common,
        Shield_Rare,
        Shield_VeryRare,
        Shield_ExtremelyRare,
        Turret_Common,
        Turret_Rare,
        Turret_VeryRare,
        Turret_ExtremelyRare,
        ForceAmp_Common,
        ForceAmp_Rare,
        ForceAmp_VeryRare,
        ForceAmp_ExtremelyRare,
        Firewall_Common,
        Firewall_Rare,
        Firewall_VeryRare,
        Firewall_ExtremelyRare,
        AntiVirus_Common,
        AntiVirus_Rare,
        AntiVirus_VeryRare,
        AntiVirus_ExtremelyRare,
        Cloaking_Common,
        Cloaking_Rare,
        Cloaking_VeryRare,
        Cloaking_ExtremelyRare,
    }

    public static enum Resonators {
        NONE,
        RESO_L1,
        RESO_L2,
        RESO_L3,
        RESO_L4,
        RESO_L5,
        RESO_L6,
        RESO_L7,
        RESO_L8
    }

    public static enum PowerCube {
        NONE,
        PowerCube_L1,
        PowerCube_L2,
        PowerCube_L3,
        PowerCube_L4,
        PowerCube_L5,
        PowerCube_L6,
        PowerCube_L7,
        PowerCube_L8,
        Recharger_L1,
        Recharger_L2,
        Recharger_L3,
        Recharger_L4,
        Recharger_L5,
        Recharger_L6,
        Recharger_L7,
        Recharger_L8
    }


    public static enum Weapons {
        NONE,
        XMP_L1,
        XMP_L2,
        XMP_L3,
        XMP_L4,
        XMP_L5,
        XMP_L6,
        XMP_L7,
        XMP_L8,
        UltraStrike_L1,
        UltraStrike_L2,
        UltraStrike_L3,
        UltraStrike_L4,
        UltraStrike_L5,
        UltraStrike_L6,
        UltraStrike_L7,
        UltraStrike_L8,
        Virus_A,
        Virus_B,
        Virus_C,
        Virus_D,
    }

    public static enum Mods {
        NONE,
        MultiHack_Common,
        MultiHack_Rare,
        MultiHack_VeryRare,
        MultiHack_ExtremelyRare,
        HeatSink_Common,
        HeatSink_Rare,
        HeatSink_VeryRare,
        HeatSink_ExtremelyRare,
        LinkAmp_Common,
        LinkAmp_Rare,
        LinkAmp_VeryRare,
        LinkAmp_ExtremelyRare,
        Shield_Common,
        Shield_Rare,
        Shield_VeryRare,
        Shield_ExtremelyRare,
        Turret_Common,
        Turret_Rare,
        Turret_VeryRare,
        Turret_ExtremelyRare,
        ForceAmp_Common,
        ForceAmp_Rare,
        ForceAmp_VeryRare,
        ForceAmp_ExtremelyRare,
        Firewall_Common,
        Firewall_Rare,
        Firewall_VeryRare,
        Firewall_ExtremelyRare,
        AntiVirus_Common,
        AntiVirus_Rare,
        AntiVirus_VeryRare,
        AntiVirus_ExtremelyRare,
        Cloaking_Common,
        Cloaking_Rare,
        Cloaking_VeryRare,
        Cloaking_ExtremelyRare,
    }



    String id;
    String name;
    String imageUrl;

}
