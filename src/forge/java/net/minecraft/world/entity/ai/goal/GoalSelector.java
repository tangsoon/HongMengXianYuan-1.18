package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final WrappedGoal NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, new Goal() {
      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean canUse() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   /** Goals currently using a particular flag */
   private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap<>(Goal.Flag.class);
   private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
   private final Supplier<ProfilerFiller> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickCount;
   private int newGoalRate = 3;

   public GoalSelector(Supplier<ProfilerFiller> pProfiler) {
      this.profiler = pProfiler;
   }

   /**
    * Add a goal to the GoalSelector with a certain priority. Lower numbers are higher priority.
    */
   public void addGoal(int pPriority, Goal pGoal) {
      this.availableGoals.add(new WrappedGoal(pPriority, pGoal));
   }

   @VisibleForTesting
   public void removeAllGoals() {
      this.availableGoals.clear();
   }

   /**
    * Remove the goal from the GoalSelector. This must be the same object as the goal you are trying to remove, which
    * may not always be accessible.
    */
   public void removeGoal(Goal pGoal) {
      this.availableGoals.stream().filter((p_25378_) -> {
         return p_25378_.getGoal() == pGoal;
      }).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
      this.availableGoals.removeIf((p_25367_) -> {
         return p_25367_.getGoal() == pGoal;
      });
   }

   /**
    * Ticks every goal in the selector.
    * Attempts to start each goal based on if it can be used, or stop it if it can't.
    */
   public void tick() {
      ProfilerFiller profilerfiller = this.profiler.get();
      profilerfiller.push("goalCleanup");
      this.getRunningGoals().filter((p_25390_) -> {
         return !p_25390_.isRunning() || p_25390_.getFlags().stream().anyMatch(this.disabledFlags::contains) || !p_25390_.canContinueToUse();
      }).forEach(Goal::stop);
      this.lockedFlags.forEach((p_25358_, p_25359_) -> {
         if (!p_25359_.isRunning()) {
            this.lockedFlags.remove(p_25358_);
         }

      });
      profilerfiller.pop();
      profilerfiller.push("goalUpdate");
      this.availableGoals.stream().filter((p_25388_) -> {
         return !p_25388_.isRunning();
      }).filter((p_25385_) -> {
         return p_25385_.getFlags().stream().noneMatch(this.disabledFlags::contains);
      }).filter((p_25380_) -> {
         return p_25380_.getFlags().stream().allMatch((p_148104_) -> {
            return this.lockedFlags.getOrDefault(p_148104_, NO_GOAL).canBeReplacedBy(p_25380_);
         });
      }).filter(WrappedGoal::canUse).forEach((p_25369_) -> {
         p_25369_.getFlags().forEach((p_148101_) -> {
            WrappedGoal wrappedgoal = this.lockedFlags.getOrDefault(p_148101_, NO_GOAL);
            wrappedgoal.stop();
            this.lockedFlags.put(p_148101_, p_25369_);
         });
         p_25369_.start();
      });
      profilerfiller.pop();
      profilerfiller.push("goalTick");
      this.getRunningGoals().forEach(WrappedGoal::tick);
      profilerfiller.pop();
   }

   public Set<WrappedGoal> getAvailableGoals() {
      return this.availableGoals;
   }

   public Stream<WrappedGoal> getRunningGoals() {
      return this.availableGoals.stream().filter(WrappedGoal::isRunning);
   }

   public void setNewGoalRate(int pNewGoalRate) {
      this.newGoalRate = pNewGoalRate;
   }

   public void disableControlFlag(Goal.Flag pFlag) {
      this.disabledFlags.add(pFlag);
   }

   public void enableControlFlag(Goal.Flag pFlag) {
      this.disabledFlags.remove(pFlag);
   }

   public void setControlFlag(Goal.Flag pFlag, boolean pEnabled) {
      if (pEnabled) {
         this.enableControlFlag(pFlag);
      } else {
         this.disableControlFlag(pFlag);
      }

   }
}