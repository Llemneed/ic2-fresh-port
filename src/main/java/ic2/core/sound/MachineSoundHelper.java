package ic2.core.sound;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public final class MachineSoundHelper {
    private MachineSoundHelper() {
    }

    /**
     * Periodically replays a machine sound on the server.
     * TODO: replace with proper client-side looping sound instances during the audio polish pass.
     */
    public static void playPeriodic(Level level, BlockPos pos, SoundEvent sound) {
        playPeriodic(level, pos, sound, 40L, 0.35F, 1.0F);
    }

    public static void playPeriodic(Level level, BlockPos pos, SoundEvent sound, long interval, float volume, float pitch) {
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
