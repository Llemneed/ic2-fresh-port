package ic2.core.sound;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public final class MachineSoundHelper {
    private MachineSoundHelper() {
    }

    public static void playLoop(Level level, BlockPos pos, SoundEvent sound) {
        playLoop(level, pos, sound, 40L, 0.35F, 1.0F);
    }

    public static void playLoop(Level level, BlockPos pos, SoundEvent sound, long interval, float volume, float pitch) {
        if (level.isClientSide || sound == null || level.getGameTime() % interval != 0L) {
            return;
        }

        level.playSound(
                null,
                pos.getX() + 0.5D,
                pos.getY() + 0.5D,
                pos.getZ() + 0.5D,
                sound,
                SoundSource.BLOCKS,
                volume,
                pitch
        );
    }
}
