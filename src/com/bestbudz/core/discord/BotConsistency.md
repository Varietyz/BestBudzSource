# Discord Bot Development Guidelines

## Core Principle: Integration First, Custom Implementation Last

When implementing Discord bot functionality, **ALWAYS** follow this hierarchy:

### 1. **Use Existing Game Systems First**
- The Discord bot is a **special player**, not a separate system
- Use the same methods that regular players use (`Quarrying.clickRock()`, `MovementHandler.process()`, etc.)
- Only add Discord bot privileges (virtual tools, auto-banking) as **enhancements** to existing systems
- Never duplicate core game logic

### 2. **Before Writing Any Custom Logic:**
```java
// ❌ WRONG: Custom quarrying logic for Discord bot
public void customBotQuarrying() {
    // Custom mining implementation
}

// ✅ CORRECT: Use existing systems with bot privileges
public void attemptMining() {
    boolean success = Quarrying.clickRock(bot, rock); // Use existing system
    if (success && isDiscordBot) {
        // Only add bot-specific enhancements here
        bot.performAutoBanking(); // Bot privilege
    }
}
```

### 3. **Discord Bot Special Privileges (Not Custom Systems):**
- **Virtual Tools**: Use `Pickaxe.TOOL_RING` without requiring physical equipment
- **Auto-Banking**: Automatically bank items when inventory fills
- **Movement Validation**: Enhanced pathfinding with region boundary checks
- **Experience Notifications**: Send level-up announcements to Discord
- **Reduced Processing**: Skip unnecessary client packets and visual updates

### 4. **Integration Checklist Before Adding Features:**

#### For Skill Systems:
1. **Does the skill already have a main interaction method?** (e.g., `Quarrying.clickRock()`, `Fisher.attemptFish()`)
   - ✅ Use it directly with `isDiscordBot` checks for privileges
   - ❌ Don't create `DiscordBotQuarrying.customMine()`

2. **Does the skill use the profession XP system?**
   - ✅ Use `Profession.addExperience()` - it already handles Discord bot detection
   - ❌ Don't create separate XP tracking

3. **Does the skill require tools/equipment?**
   - ✅ Add virtual tool privilege in existing tool detection
   - ❌ Don't bypass the entire tool system

#### For Movement Systems:
1. **Use existing `MovementHandler.process()`**
   - ✅ Add region boundary validation to prevent crashes
   - ✅ Use existing pathfinding with enhanced error handling
   - ❌ Don't create separate movement logic

2. **For Random Walking:**
   - ✅ Use `Region.canMove()` to validate destinations
   - ✅ Check `Region.getRegion(x, y) != null` before movement
   - ❌ Don't generate coordinates without validation

#### For Banking/Inventory:
1. **Use existing banking interfaces**
   - ✅ Extend `Box.add()` to auto-bank for Discord bot
   - ❌ Don't create separate inventory systems

### 5. **Code Pattern Examples:**

#### ✅ CORRECT Integration Pattern:
```java
public static boolean clickRock(Stoner stoner, RSObject object) {
    // Standard validation for all players
    if (stoner.getProfession().locked() || object == null) {
        return false;
    }
    
    // Discord bot privilege: virtual tools
    boolean isDiscordBot = isDiscordBot(stoner);
    Pickaxe pickaxe = isDiscordBot ? Pickaxe.TOOL_RING : Pickaxe.get(stoner);
    
    // Use same task system for all players
    TaskQueue.queue(new Task(/* standard quarrying task */));
    
    return true;
}
```

#### ❌ WRONG Custom System Pattern:
```java
// This creates duplicate systems and conflicts
public class DiscordBotQuarrying {
    public void customMiningLoop() {
        // Custom XP calculation
        // Custom reward system  
        // Custom task management
        // This duplicates existing Quarrying.java logic!
    }
}
```

### 6. **Exception Handling for Discord Bot:**
- **Movement**: Validate regions exist before pathfinding
- **Skills**: Use existing profession locks and validation
- **Banking**: Enhance existing inventory management
- **Combat**: Use existing combat systems with bot privileges

### 7. **Testing Integration:**
1. **Does the feature work for regular players?** ✅ Use that system
2. **Does the Discord bot gain the same benefits as players?** ✅ Add privileges to existing system
3. **Are there conflicts or duplicate rewards?** ❌ Remove custom logic, use integration

### 8. **Performance Considerations:**
- Discord bot doesn't need visual updates (`SendStonerUpdate`, `SendNPCUpdate`)
- Discord bot doesn't need client packet processing
- Discord bot still needs proper `reset()` and `process()` for visibility to other players

## Summary
**The Discord bot should feel like a privileged player, not a separate game entity.** 

Every Discord bot feature should answer: *"How would I give a regular player this same capability?"* Then implement it that way, with Discord bot privileges added as enhancements, not replacements.

This approach ensures:
- ✅ Consistent game mechanics
- ✅ No duplicate/conflicting systems  
- ✅ Easier maintenance and debugging
- ✅ Better integration with future features
- ✅ Reduced complexity and bugs