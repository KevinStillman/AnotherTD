package com.keviqn;

import com.badlogic.gdx.math.MathUtils;

public class CombatUtils {
    public static float calculateDamage(float attackerAttack, float attackerStrength, float defenderDefense) {
        float accuracyRoll = MathUtils.random(0, attackerAttack);
        float damageRoll = MathUtils.random(0, attackerStrength);
        return (accuracyRoll > defenderDefense) ? damageRoll : 0;
    }
}
