```
Server Folder Architecture
├─ 📂 data
│   ├─ 📂 clan
│   │   ├─ 📄 bestbudz.cla
│   │   └─ 📄 jaybane.cla
│   ├─ 📂 database
│   │   ├─ 📄 playerdata.db
│   │   ├─ 📄 playerdata.db-shm
│   │   └─ 📄 playerdata.db-wal
│   ├─ 📂 def
│   │   ├─ 📂 clues
│   │   │   └─ 🔧 clue_scroll.json
│   │   ├─ 📄 interface
│   │   ├─ 📂 items
│   │   │   ├─ 📄 EquipmentDefinitions.xml
│   │   │   ├─ 📄 FoodDefinitions.xml
│   │   │   ├─ 📄 ItemBonusDefinitions.xml
│   │   │   ├─ 📄 ItemDefinitions.xml
│   │   │   ├─ 📄 PotionDefinitions.xml
│   │   │   ├─ 📄 SagittariusVigourDefinitions.xml
│   │   │   ├─ 📄 SagittariusWeaponDefinitions.xml
│   │   │   ├─ 📄 ShopDefinitions.xml
│   │   │   ├─ 📄 SpecialAssaultDefinitions.xml
│   │   │   └─ 📄 WeaponDefinitions.xml
│   │   ├─ 📂 mage
│   │   │   └─ 📄 CombatSpellDefinitions.xml
│   │   ├─ 📄 music.txt
│   │   ├─ 📄 musiclengths.txt
│   │   ├─ 📂 npcs
│   │   │   ├─ 📄 Chex Combat Defs.xml
│   │   │   ├─ 📄 DropChances.txt
│   │   │   ├─ 📄 ItemDropDefinitions.xml
│   │   │   ├─ 📄 NpcCombatDefinitions.xml
│   │   │   ├─ 📄 NpcDefinitions.xml
│   │   │   └─ 📄 NpcSpawnDefinitions.xml
│   │   ├─ 📄 ObjectAlternates.txt
│   │   └─ 📂 professions
│   │       ├─ 🔧 exercisement1.json
│   │       └─ 🔧 exercisementment.json
│   └─ 📂 map
│       ├─ 📄 map_index
│       └─ 📂 objectdata
│           ├─ 📄 loc.dat
│           └─ 📄 loc.idx
├─ 📂 lib
│   ├─ 📄 gson-2.2.2-sources.jar
│   ├─ 📄 gson-2.2.2.jar
│   ├─ 📄 javacord-3.0.6-shaded.jar
│   ├─ 📄 json-lib-2.4-jdk15.jar
│   ├─ 📄 json-simple-1.1.1.jar
│   └─ 📄 xpp3_min-1.1.4c.jar
└─ 📂 src
    └─ 📂 com
        ├─ 📂 bestbudz
        │   ├─ 📄 BestbudzConstants.java
        │   ├─ 📂 core
        │   │   ├─ 📂 cache
        │   │   │   ├─ 📄 ByteStream.java
        │   │   │   ├─ 📄 ByteStreamExt.java
        │   │   │   ├─ 📄 CustomInterfaces.java
        │   │   │   ├─ 📂 map
        │   │   │   │   ├─ 📄 Door.class
        │   │   │   │   ├─ 📄 Door.java
        │   │   │   │   ├─ 📄 Doors.java
        │   │   │   │   ├─ 📄 DoubleDoor.class
        │   │   │   │   ├─ 📄 DoubleDoor.java
        │   │   │   │   ├─ 📄 Ladder$LadderType.class
        │   │   │   │   ├─ 📄 Ladder.class
        │   │   │   │   ├─ 📄 MapConstants.class
        │   │   │   │   ├─ 📄 MapConstants.java
        │   │   │   │   ├─ 📄 MapLoading.class
        │   │   │   │   ├─ 📄 MapLoading.java
        │   │   │   │   ├─ 📄 ObjectDef.class
        │   │   │   │   ├─ 📄 ObjectDef.java
        │   │   │   │   ├─ 📄 QueuedDoor.class
        │   │   │   │   ├─ 📄 QueuedDoor.java
        │   │   │   │   ├─ 📄 Region.class
        │   │   │   │   ├─ 📄 Region.java
        │   │   │   │   ├─ 📄 RSInterface.java
        │   │   │   │   ├─ 📄 RSObject.class
        │   │   │   │   ├─ 📄 RSObject.java
        │   │   │   │   ├─ 📄 Tile.class
        │   │   │   │   └─ 📄 Tile.java
        │   │   │   └─ 📄 MemoryArchive.java
        │   │   ├─ 📂 definitions
        │   │   │   ├─ 📄 CombatSpellDefinition.java
        │   │   │   ├─ 📄 EquipmentDefinition.java
        │   │   │   ├─ 📄 FoodDefinition.java
        │   │   │   ├─ 📄 ItemBonusDefinition.java
        │   │   │   ├─ 📄 ItemDefinition.java
        │   │   │   ├─ 📄 ItemDropDefinition.java
        │   │   │   ├─ 📄 NpcCombatDefinition.java
        │   │   │   ├─ 📄 NpcDefinition.java
        │   │   │   ├─ 📄 NpcSpawnDefinition.java
        │   │   │   ├─ 📄 PotionDefinition.java
        │   │   │   ├─ 📄 SagittariusDefinition.java
        │   │   │   ├─ 📄 SagittariusVigourDefinition.java
        │   │   │   ├─ 📄 SagittariusWeaponDefinition.java
        │   │   │   ├─ 📄 ShopDefinition.java
        │   │   │   ├─ 📄 SpecialAssaultDefinition.java
        │   │   │   └─ 📄 WeaponDefinition.java
        │   │   ├─ 📂 discord
        │   │   │   ├─ 📂 core
        │   │   │   │   ├─ 📄 DiscordBot.java
        │   │   │   │   ├─ 📄 DiscordConfig.java
        │   │   │   │   ├─ 📄 DiscordPlugin.java
        │   │   │   │   └─ 📄 DiscordServerIntegration.java
        │   │   │   ├─ 📄 DiscordManager.java
        │   │   │   ├─ 📂 events
        │   │   │   │   ├─ 📄 DiscordEvent.java
        │   │   │   │   └─ 📄 EventHandler.java
        │   │   │   ├─ 📂 messaging
        │   │   │   │   ├─ 📄 DiscordMessage.java
        │   │   │   │   ├─ 📄 DiscordMessageManager.java
        │   │   │   │   └─ 📄 DiscordMessageService.java
        │   │   │   ├─ 📄 PluginManager.java
        │   │   │   ├─ 📂 plugins
        │   │   │   │   ├─ 📄 ChatBridgePlugin.java
        │   │   │   │   └─ 📄 ExamplePlugin.java
        │   │   │   ├─ 📂 stonerbot
        │   │   │   │   ├─ 📂 automations
        │   │   │   │   │   ├─ 📄 BotPrivileges.java
        │   │   │   │   │   ├─ 📄 DiscordBotEmotes.java
        │   │   │   │   │   ├─ 📄 DiscordBotObjectHandler.java
        │   │   │   │   │   ├─ 📄 DiscordBotPersistence.java
        │   │   │   │   │   └─ 📄 DiscordBotQuarrying.java
        │   │   │   │   ├─ 📄 DiscordBotDefaults.java
        │   │   │   │   ├─ 📄 DiscordBotStoner.java
        │   │   │   │   ├─ 📂 functions
        │   │   │   │   │   ├─ 📄 DiscordBotActions.java
        │   │   │   │   │   └─ 📄 DiscordBotChat.java
        │   │   │   │   ├─ 📂 grades
        │   │   │   │   │   └─ 📄 DiscordBotGrades.java
        │   │   │   │   ├─ 📂 handling
        │   │   │   │   └─ 📂 state
        │   │   │   │       ├─ 📄 DiscordBotAppearance.java
        │   │   │   │       ├─ 📄 DiscordBotItemHandler.java
        │   │   │   │       └─ 📄 DiscordBotLocation.java
        │   │   │   └─ 📂 util
        │   │   │       └─ 📄 DiscordUtil.java
        │   │   ├─ 📄 GameThread.java
        │   │   ├─ 📄 LoginThread.java
        │   │   ├─ 📂 network
        │   │   │   ├─ 📄 ChannelHandler.java
        │   │   │   ├─ 📄 ClientMap.java
        │   │   │   ├─ 📄 ISAACCipher.java
        │   │   │   ├─ 📂 login
        │   │   │   │   ├─ 📄 Decoder.java
        │   │   │   │   ├─ 📄 Encoder.java
        │   │   │   │   └─ 📄 LoginDecoder.java
        │   │   │   ├─ 📄 PipelineFactory.java
        │   │   │   ├─ 📄 ReceivedPacket.java
        │   │   │   └─ 📄 StreamBuffer.java
        │   │   ├─ 📄 NetworkThread.java
        │   │   ├─ 📂 security
        │   │   │   └─ 📄 PasswordEncryption.java
        │   │   ├─ 📂 task
        │   │   │   ├─ 📂 impl
        │   │   │   │   ├─ 📄 AntifireTask.java
        │   │   │   │   ├─ 📄 CrossGangPlankTask.java
        │   │   │   │   ├─ 📄 DigTask.java
        │   │   │   │   ├─ 📄 EntityInteractionTask.java
        │   │   │   │   ├─ 📄 FinishTeleportingTask.java
        │   │   │   │   ├─ 📄 FollowToEntityTask.java
        │   │   │   │   ├─ 📄 ForceMovementController.java
        │   │   │   │   ├─ 📄 ForceMovementTask.java
        │   │   │   │   ├─ 📄 ForceMoveTask.java
        │   │   │   │   ├─ 📄 GraphicTask.java
        │   │   │   │   ├─ 📄 HarvestTask.java
        │   │   │   │   ├─ 📄 HitTask.java
        │   │   │   │   ├─ 📄 HopDitchTask.java
        │   │   │   │   ├─ 📄 JumpObjectTask.java
        │   │   │   │   ├─ 📄 MobDeathTask.java
        │   │   │   │   ├─ 📄 MobWalkTask.java
        │   │   │   │   ├─ 📄 ObeliskTick.java
        │   │   │   │   ├─ 📄 OpenChestTask.java
        │   │   │   │   ├─ 📄 ProductionTask.java
        │   │   │   │   ├─ 📄 PullLeverTask.java
        │   │   │   │   ├─ 📄 RandomTalkTask.java
        │   │   │   │   ├─ 📄 RegenerateProfessionTask.java
        │   │   │   │   ├─ 📄 ReplaceObjectTask.java
        │   │   │   │   ├─ 📄 ShearingTask.java
        │   │   │   │   ├─ 📄 StonerBackupTask.java
        │   │   │   │   ├─ 📄 StonerDeathTask.java
        │   │   │   │   ├─ 📄 TaskIdentifier.java
        │   │   │   │   ├─ 📄 TeleOtherTask.java
        │   │   │   │   ├─ 📄 TickDoorTask.java
        │   │   │   │   ├─ 📄 WalkThroughDoorTask.java
        │   │   │   │   ├─ 📄 WalkThroughDoubleDoorTask.java
        │   │   │   │   └─ 📄 WalkToTask.java
        │   │   │   ├─ 📄 RunOnceTask.java
        │   │   │   ├─ 📄 Task.java
        │   │   │   ├─ 📄 TaskIdentifier.java
        │   │   │   └─ 📄 TaskQueue.java
        │   │   └─ 📂 util
        │   │       ├─ 📄 Benchmarker.java
        │   │       ├─ 📂 chance
        │   │       │   ├─ 📄 Chance.java
        │   │       │   ├─ 📄 ChanceTester.java
        │   │       │   ├─ 📄 WeightedChance.java
        │   │       │   └─ 📄 WeightedObject.java
        │   │       ├─ 📄 GameDefinitionLoader.java
        │   │       ├─ 📄 ItemNames.java
        │   │       ├─ 📄 LineCounter.java
        │   │       ├─ 📂 logger
        │   │       │   └─ 📄 StonerLogger.java
        │   │       ├─ 📄 MobUpdateList.java
        │   │       ├─ 📄 NameUtil.java
        │   │       ├─ 📄 Stopwatch.java
        │   │       ├─ 📄 SystemLogger.java
        │   │       ├─ 📄 TimeStamp.java
        │   │       ├─ 📄 UpdateableMob.java
        │   │       └─ 📄 Utility.java
        │   ├─ 📄 GameDataLoader.java
        │   ├─ 📂 net
        │   ├─ 📂 rs2
        │   │   ├─ 📂 auto
        │   │   │   └─ 📂 combat
        │   │   │       └─ 📄 AutoCombat.java
        │   │   ├─ 📂 content
        │   │   │   ├─ 📂 achievements
        │   │   │   │   ├─ 📄 AchievementButtons.java
        │   │   │   │   ├─ 📄 AchievementHandler.java
        │   │   │   │   ├─ 📄 AchievementInterface.java
        │   │   │   │   └─ 📄 AchievementList.java
        │   │   │   ├─ 📄 Advance.java
        │   │   │   ├─ 📄 Announcement.java
        │   │   │   ├─ 📄 ArmourSets.java
        │   │   │   ├─ 📂 bank
        │   │   │   │   ├─ 📄 Bank.java
        │   │   │   │   ├─ 📄 DockBank.java
        │   │   │   │   └─ 📄 DockBox.java
        │   │   │   ├─ 📄 Box.java
        │   │   │   ├─ 📂 clanchat
        │   │   │   │   ├─ 📄 Clan.java
        │   │   │   │   ├─ 📄 ClanManager.java
        │   │   │   │   └─ 📄 ClanRank.java
        │   │   │   ├─ 📂 cluescroll
        │   │   │   │   ├─ 📄 Clue.java
        │   │   │   │   ├─ 📄 ClueDifficulty.java
        │   │   │   │   ├─ 📄 ClueScroll.java
        │   │   │   │   ├─ 📄 ClueScrollManager.java
        │   │   │   │   └─ 📂 scroll
        │   │   │   │       ├─ 📄 EmoteScroll.java
        │   │   │   │       └─ 📄 MapScroll.java
        │   │   │   ├─ 📂 combat
        │   │   │   │   ├─ 📄 Combat.java
        │   │   │   │   ├─ 📄 CombatConstants.java
        │   │   │   │   ├─ 📄 CombatEffect.java
        │   │   │   │   ├─ 📄 CombatInterface.java
        │   │   │   │   ├─ 📂 formula
        │   │   │   │   │   ├─ 📄 FormulaData.java
        │   │   │   │   │   ├─ 📄 MageFormulas.java
        │   │   │   │   │   ├─ 📄 MeleeFormulas.java
        │   │   │   │   │   └─ 📄 RangeFormulas.java
        │   │   │   │   ├─ 📄 Hit.java
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 Assault.java
        │   │   │   │   │   ├─ 📄 CombatCurse.java
        │   │   │   │   │   ├─ 📄 CombatEffect.java
        │   │   │   │   │   ├─ 📄 DamageMap.java
        │   │   │   │   │   ├─ 📄 Mage.java
        │   │   │   │   │   ├─ 📄 Melee.java
        │   │   │   │   │   ├─ 📄 PoisonData.java
        │   │   │   │   │   ├─ 📄 PoisonWeapons.java
        │   │   │   │   │   ├─ 📄 RingOfRecoil.java
        │   │   │   │   │   ├─ 📄 Sagittarius.java
        │   │   │   │   │   ├─ 📄 Skulling.java
        │   │   │   │   │   ├─ 📄 SpecialAssault.java
        │   │   │   │   │   └─ 📄 StonerDrops.java
        │   │   │   │   ├─ 📂 special
        │   │   │   │   │   ├─ 📂 effects
        │   │   │   │   │   │   ├─ 📄 AbyssalTentacleEffect.java
        │   │   │   │   │   │   ├─ 📄 AbyssalWhipEffect.java
        │   │   │   │   │   │   ├─ 📄 BandosGodswordEffect.java
        │   │   │   │   │   │   ├─ 📄 BarrelchestAnchorEffect.java
        │   │   │   │   │   │   ├─ 📄 DragonScimitarEffect.java
        │   │   │   │   │   │   ├─ 📄 DragonSpearEffect.java
        │   │   │   │   │   │   ├─ 📄 SaradominGodswordEffect.java
        │   │   │   │   │   │   ├─ 📄 ToxicBlowpipeEffect.java
        │   │   │   │   │   │   ├─ 📄 ZamorakianHastaEffect.java
        │   │   │   │   │   │   └─ 📄 ZamorakianSpearEffect.java
        │   │   │   │   │   ├─ 📄 Special.java
        │   │   │   │   │   ├─ 📄 SpecialAssaultHandler.java
        │   │   │   │   │   └─ 📂 specials
        │   │   │   │   │       ├─ 📄 AbyssalTentacleSpecialAssault.java
        │   │   │   │   │       ├─ 📄 AbyssalWhipSpecialAssault.java
        │   │   │   │   │       ├─ 📄 AnchorSpecialAssault.java
        │   │   │   │   │       ├─ 📄 ArmadylCrossbowSpecialAssault.java
        │   │   │   │   │       ├─ 📄 ArmadylGodswordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 BandosGodswordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DarkBowSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonBattleaxeSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonClawsSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonDaggerSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonHalberdSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonLongswordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonMaceSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonScimitarSpecialAssault.java
        │   │   │   │   │       ├─ 📄 DragonSpearSpecialAssault.java
        │   │   │   │   │       ├─ 📄 GraniteMaulSpecialAssault.java
        │   │   │   │   │       ├─ 📄 MageShortbowInfusedSpecialAssault.java
        │   │   │   │   │       ├─ 📄 MageShortbowSpecialAssault.java
        │   │   │   │   │       ├─ 📄 SaradominGodswordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 SaradominSwordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 StaffOfDeadSpecialAssault.java
        │   │   │   │   │       ├─ 📄 ToxicBlowpipeSpecialAssault.java
        │   │   │   │   │       ├─ 📄 ZamorakGodswordSpecialAssault.java
        │   │   │   │   │       ├─ 📄 ZamorakianHastaSpecialAssault.java
        │   │   │   │   │       └─ 📄 ZamorakianSpearSpecialAssault.java
        │   │   │   │   └─ 📄 StonerCombatInterface.java
        │   │   │   ├─ 📂 consumables
        │   │   │   │   ├─ 📄 Consumables.java
        │   │   │   │   ├─ 📄 ConsumableType.java
        │   │   │   │   └─ 📄 SpecialConsumables.java
        │   │   │   ├─ 📄 CreationHandle.java
        │   │   │   ├─ 📄 CrystalChest.java
        │   │   │   ├─ 📂 dialogue
        │   │   │   │   ├─ 📄 Dialogue.java
        │   │   │   │   ├─ 📄 DialogueConstants.java
        │   │   │   │   ├─ 📄 DialogueManager.java
        │   │   │   │   ├─ 📄 Emotion.java
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 AchievementDialogue.java
        │   │   │   │   │   ├─ 📄 AdvanceDialogue.java
        │   │   │   │   │   ├─ 📄 BestBudzDialogue.java
        │   │   │   │   │   ├─ 📄 ChangePasswordDialogue.java
        │   │   │   │   │   ├─ 📄 ConfirmDialogue.java
        │   │   │   │   │   ├─ 📄 ConsumerTeleport.java
        │   │   │   │   │   ├─ 📄 DecantingDialogue.java
        │   │   │   │   │   ├─ 📄 DunceDialogue.java
        │   │   │   │   │   ├─ 📄 EmblemDialogue.java
        │   │   │   │   │   ├─ 📄 GenieResetDialogue.java
        │   │   │   │   │   ├─ 📄 GodwarsOption.java
        │   │   │   │   │   ├─ 📄 HariDialogue.java
        │   │   │   │   │   ├─ 📄 KamfreeDialogue.java
        │   │   │   │   │   ├─ 📄 KolodionDialogue.java
        │   │   │   │   │   ├─ 📄 MakeoverMage.java
        │   │   │   │   │   ├─ 📄 MembershipDialogue.java
        │   │   │   │   │   ├─ 📄 NeiveDialogue.java
        │   │   │   │   │   ├─ 📄 OttoGodblessed.java
        │   │   │   │   │   ├─ 📄 OziachDialogue.java
        │   │   │   │   │   ├─ 📄 PilesDialogue.java
        │   │   │   │   │   ├─ 📄 SailorDialogue.java
        │   │   │   │   │   ├─ 📄 StaffTitleDialogue.java
        │   │   │   │   │   ├─ 📂 teleport
        │   │   │   │   │   │   ├─ 📄 GloryDialogue.java
        │   │   │   │   │   │   ├─ 📄 RingOfDuelingDialogue.java
        │   │   │   │   │   │   ├─ 📄 RingOfSlayingDialogue.java
        │   │   │   │   │   │   ├─ 📄 SpiritTree.java
        │   │   │   │   │   │   └─ 📄 WildernessLever.java
        │   │   │   │   │   ├─ 📄 TzhaarMejKahDialogue.java
        │   │   │   │   │   ├─ 📄 UseBankDialogue.java
        │   │   │   │   │   ├─ 📄 VannakaDialogue.java
        │   │   │   │   │   └─ 📄 WeaponGameDialogue.java
        │   │   │   │   ├─ 📄 OneLineDialogue.java
        │   │   │   │   └─ 📄 OptionDialogue.java
        │   │   │   ├─ 📄 DropTable.java
        │   │   │   ├─ 📂 dwarfcannon
        │   │   │   │   ├─ 📄 DwarfCannon.java
        │   │   │   │   └─ 📄 DwarfMultiCannon.java
        │   │   │   ├─ 📄 EasterRing.java
        │   │   │   ├─ 📄 Emotes.java
        │   │   │   ├─ 📂 exercisement
        │   │   │   │   ├─ 📄 Exercisement.java
        │   │   │   │   └─ 📂 obstacle
        │   │   │   │       ├─ 📂 interaction
        │   │   │   │       │   ├─ 📄 ClimbInteraction.java
        │   │   │   │       │   ├─ 📄 ClimbOverInteraction.java
        │   │   │   │       │   ├─ 📄 ObstacleInteraction.java
        │   │   │   │       │   ├─ 📂 rooftop
        │   │   │   │       │   │   ├─ 📂 ardougne
        │   │   │   │       │   │   │   ├─ 📄 ArdougneJumpGapInteraction.java
        │   │   │   │       │   │   │   ├─ 📄 ArdougneRoofJumpInteraction.java
        │   │   │   │       │   │   │   ├─ 📄 ArdougneRoofJumpInteraction2.java
        │   │   │   │       │   │   │   ├─ 📄 ArdougneSteepRoofInteraction.java
        │   │   │   │       │   │   │   └─ 📄 ArdougneWallClimbInteraction.java
        │   │   │   │       │   │   └─ 📂 seers
        │   │   │   │       │   │       ├─ 📄 SeersJumpGapInteraction.java
        │   │   │   │       │   │       ├─ 📄 SeersJumpGapInteraction2.java
        │   │   │   │       │   │       └─ 📄 SeersWallClimbInteraction.java
        │   │   │   │       │   ├─ 📄 RopeSwingInteraction.java
        │   │   │   │       │   ├─ 📄 SteppingStonesInteraction.java
        │   │   │   │       │   └─ 📄 WalkInteraction.java
        │   │   │   │       ├─ 📄 Obstacle.java
        │   │   │   │       └─ 📄 ObstacleType.java
        │   │   │   ├─ 📄 FountainOfRune.java
        │   │   │   ├─ 📄 GenieLamp.java
        │   │   │   ├─ 📄 GenieReset.java
        │   │   │   ├─ 📂 interfaces
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 AchievementTab.java
        │   │   │   │   │   ├─ 📄 BossInterface.java
        │   │   │   │   │   ├─ 📄 CommandInterface.java
        │   │   │   │   │   ├─ 📄 CreditTab.java
        │   │   │   │   │   ├─ 📄 MinigameInterface.java
        │   │   │   │   │   ├─ 📄 MiscInterfaces.java
        │   │   │   │   │   ├─ 📄 ModCommandsInterface.java
        │   │   │   │   │   ├─ 📄 OtherInterface.java
        │   │   │   │   │   ├─ 📄 PointsInterface.java
        │   │   │   │   │   ├─ 📄 ProfessioningInterface.java
        │   │   │   │   │   ├─ 📄 PvPInterface.java
        │   │   │   │   │   ├─ 📄 QuestTab.java
        │   │   │   │   │   └─ 📄 TrainingInterface.java
        │   │   │   │   └─ 📄 InterfaceHandler.java
        │   │   │   ├─ 📂 io
        │   │   │   │   ├─ 📄 ItemCheck.java
        │   │   │   │   └─ 📂 sqlite
        │   │   │   │       ├─ 📄 AntiRollbackManager.java
        │   │   │   │       ├─ 📄 BulkPlayerImporter.java
        │   │   │   │       ├─ 📄 GracefulShutdownHook.java
        │   │   │   │       ├─ 📄 SaveCache.java
        │   │   │   │       ├─ 📄 SaveConstants.java
        │   │   │   │       ├─ 📄 SaveWorker.java
        │   │   │   │       ├─ 📄 SQLiteDB.java
        │   │   │   │       ├─ 📄 SQLiteUtils.java
        │   │   │   │       ├─ 📄 StonerLoadUtil.java
        │   │   │   │       ├─ 📄 StonerSave.java
        │   │   │   │       └─ 📄 StonerSaveUtil.java
        │   │   │   ├─ 📄 ItemCreation.java
        │   │   │   ├─ 📄 ItemInteraction.java
        │   │   │   ├─ 📄 ItemOpening.java
        │   │   │   ├─ 📄 LoyaltyShop.java
        │   │   │   ├─ 📂 membership
        │   │   │   │   ├─ 📄 AdvancementBonds.java
        │   │   │   │   ├─ 📄 CreditHandler.java
        │   │   │   │   ├─ 📄 CreditPurchase.java
        │   │   │   │   ├─ 📄 Handle.java
        │   │   │   │   ├─ 📄 MysteryBoxMinigame.java
        │   │   │   │   └─ 📄 RankHandler.java
        │   │   │   ├─ 📂 minigames
        │   │   │   │   ├─ 📂 barrows
        │   │   │   │   │   └─ 📄 Barrows.java
        │   │   │   │   ├─ 📂 clanwars
        │   │   │   │   │   ├─ 📄 ClanWarsConstants.java
        │   │   │   │   │   ├─ 📄 ClanWarsFFA.java
        │   │   │   │   │   └─ 📄 ClanWarsFFAController.java
        │   │   │   │   ├─ 📂 duelarena
        │   │   │   │   │   ├─ 📄 DuelArenaController.java
        │   │   │   │   │   ├─ 📄 DuelArenaForfeit.java
        │   │   │   │   │   ├─ 📄 Dueling.java
        │   │   │   │   │   ├─ 📄 DuelingConstants.java
        │   │   │   │   │   ├─ 📄 DuelingController.java
        │   │   │   │   │   ├─ 📄 DuelingManager.java
        │   │   │   │   │   ├─ 📄 DuelStakeController.java
        │   │   │   │   │   └─ 📄 StakingContainer.java
        │   │   │   │   ├─ 📂 f2parena
        │   │   │   │   │   ├─ 📄 F2PArena.java
        │   │   │   │   │   ├─ 📄 F2PArenaConstants.java
        │   │   │   │   │   └─ 📄 F2PArenaController.java
        │   │   │   │   ├─ 📂 fightcave
        │   │   │   │   │   ├─ 📄 TzharrController.java
        │   │   │   │   │   ├─ 📄 TzharrData.java
        │   │   │   │   │   ├─ 📄 TzharrDetails.java
        │   │   │   │   │   └─ 📄 TzharrGame.java
        │   │   │   │   ├─ 📂 fightpits
        │   │   │   │   │   ├─ 📄 FightPits.java
        │   │   │   │   │   ├─ 📄 FightPitsConstants.java
        │   │   │   │   │   ├─ 📄 FightPitsController.java
        │   │   │   │   │   └─ 📄 FightPitsWaitingController.java
        │   │   │   │   ├─ 📂 godwars
        │   │   │   │   │   ├─ 📄 GodWars.java
        │   │   │   │   │   ├─ 📄 GodWarsController.java
        │   │   │   │   │   └─ 📄 GodWarsData.java
        │   │   │   │   ├─ 📂 pestcontrol
        │   │   │   │   │   ├─ 📂 monsters
        │   │   │   │   │   │   ├─ 📄 Portal.java
        │   │   │   │   │   │   ├─ 📄 Shifter.java
        │   │   │   │   │   │   ├─ 📄 Spinner.java
        │   │   │   │   │   │   └─ 📄 Splatter.java
        │   │   │   │   │   ├─ 📄 Pest.java
        │   │   │   │   │   ├─ 📄 PestControl.java
        │   │   │   │   │   ├─ 📄 PestControlConstants.java
        │   │   │   │   │   ├─ 📄 PestControlController.java
        │   │   │   │   │   ├─ 📄 PestControlGame.java
        │   │   │   │   │   └─ 📄 PestWaitingRoomController.java
        │   │   │   │   ├─ 📂 plunder
        │   │   │   │   │   ├─ 📄 PlunderConstants.java
        │   │   │   │   │   ├─ 📄 PlunderController.java
        │   │   │   │   │   ├─ 📄 PyramidPlunder.java
        │   │   │   │   │   └─ 📂 tasks
        │   │   │   │   │       ├─ 📄 LootUrnTask.java
        │   │   │   │   │       └─ 📄 PicklockDoorTask.java
        │   │   │   │   ├─ 📄 StonerMinigames.java
        │   │   │   │   ├─ 📂 warriorsguild
        │   │   │   │   │   ├─ 📄 ArmourAnimator.java
        │   │   │   │   │   ├─ 📄 CyclopsRoom.java
        │   │   │   │   │   └─ 📄 TokenTask.java
        │   │   │   │   └─ 📂 weapongame
        │   │   │   │       ├─ 📄 WeaponGame.java
        │   │   │   │       ├─ 📄 WeaponGameConstants.java
        │   │   │   │       ├─ 📄 WeaponGameController.java
        │   │   │   │       ├─ 📄 WeaponGameStore.java
        │   │   │   │       └─ 📄 WeaponLobbyController.java
        │   │   │   ├─ 📂 moderation
        │   │   │   │   ├─ 📄 DockStaff.java
        │   │   │   │   ├─ 📄 StaffDBUtils.java
        │   │   │   │   └─ 📄 StaffTab.java
        │   │   │   ├─ 📄 MoneyPouch.java
        │   │   │   ├─ 📄 MysteryBox.java
        │   │   │   ├─ 📄 NpcGuide.java
        │   │   │   ├─ 📂 pets
        │   │   │   │   └─ 📄 BossPets.java
        │   │   │   ├─ 📄 PriceChecker.java
        │   │   │   ├─ 📄 PrivateMessaging.java
        │   │   │   ├─ 📂 profession
        │   │   │   │   ├─ 📂 petmaster
        │   │   │   │   │   ├─ 📄 HomeStalls.java
        │   │   │   │   │   └─ 📄 WallSafes.java
        │   │   │   │   ├─ 📂 bankstanding
        │   │   │   │   │   ├─ 📄 BankStanding.java
        │   │   │   │   │   └─ 📄 BankStandingConstants.java
        │   │   │   │   ├─ 📂 fisher
        │   │   │   │   │   ├─ 📄 FishableData.java
        │   │   │   │   │   ├─ 📄 Fisher.java
        │   │   │   │   │   └─ 📄 ToolData.java
        │   │   │   │   ├─ 📂 foodie
        │   │   │   │   │   ├─ 📄 Foodie.java
        │   │   │   │   │   └─ 📄 FoodieData.java
        │   │   │   │   ├─ 📂 forging
        │   │   │   │   │   ├─ 📄 Forging.java
        │   │   │   │   │   ├─ 📄 ForgingConstants.java
        │   │   │   │   │   ├─ 📄 ForgingTask.java
        │   │   │   │   │   ├─ 📄 Smelting.java
        │   │   │   │   │   └─ 📄 SmeltingData.java
        │   │   │   │   ├─ 📂 handiness
        │   │   │   │   │   ├─ 📄 AmuletStringing.java
        │   │   │   │   │   ├─ 📄 ArmourCreation.java
        │   │   │   │   │   ├─ 📄 BoltTipData.java
        │   │   │   │   │   ├─ 📄 Craftable.java
        │   │   │   │   │   ├─ 📄 Flax.java
        │   │   │   │   │   ├─ 📄 Glass.java
        │   │   │   │   │   ├─ 📄 GlassBlowing.java
        │   │   │   │   │   ├─ 📄 GlassMelting.java
        │   │   │   │   │   ├─ 📄 Handiness.java
        │   │   │   │   │   ├─ 📄 HandinessType.java
        │   │   │   │   │   ├─ 📄 HideTanData.java
        │   │   │   │   │   ├─ 📄 HideTanning.java
        │   │   │   │   │   ├─ 📄 Jewelry.java
        │   │   │   │   │   ├─ 📄 JewelryCreationTask.java
        │   │   │   │   │   ├─ 📄 Spinnable.java
        │   │   │   │   │   └─ 📄 WheelSpinning.java
        │   │   │   │   ├─ 📂 handinessnew
        │   │   │   │   │   ├─ 📂 craftable
        │   │   │   │   │   │   ├─ 📄 Craftable.java
        │   │   │   │   │   │   ├─ 📄 CraftableItem.java
        │   │   │   │   │   │   └─ 📂 impl
        │   │   │   │   │   │       ├─ 📄 Gem.java
        │   │   │   │   │   │       ├─ 📄 Hide.java
        │   │   │   │   │   │       └─ 📄 Leather.java
        │   │   │   │   │   └─ 📄 Handiness.java
        │   │   │   │   ├─ 📂 hunter
        │   │   │   │   │   └─ 📄 Impling.java
        │   │   │   │   ├─ 📂 lumbering
        │   │   │   │   │   ├─ 📄 LumberingAxeData.java
        │   │   │   │   │   ├─ 📄 LumberingTask.java
        │   │   │   │   │   ├─ 📄 LumberingTreeData.java
        │   │   │   │   │   └─ 📄 StumpTask.java
        │   │   │   │   ├─ 📂 mage
        │   │   │   │   │   ├─ 📄 Autocast.java
        │   │   │   │   │   ├─ 📂 effects
        │   │   │   │   │   │   ├─ 📄 BindEffect.java
        │   │   │   │   │   │   ├─ 📄 BloodBarrageEffect.java
        │   │   │   │   │   │   ├─ 📄 BloodBlitzEffect.java
        │   │   │   │   │   │   ├─ 📄 BloodBurstEffect.java
        │   │   │   │   │   │   ├─ 📄 BloodRushEffect.java
        │   │   │   │   │   │   ├─ 📄 ClawsOfGuthixEffect.java
        │   │   │   │   │   │   ├─ 📄 EntangleEffect.java
        │   │   │   │   │   │   ├─ 📄 FlamesOfZamorakEffect.java
        │   │   │   │   │   │   ├─ 📄 IceBarrageEffect.java
        │   │   │   │   │   │   ├─ 📄 IceBlitzEffect.java
        │   │   │   │   │   │   ├─ 📄 IceBurstEffect.java
        │   │   │   │   │   │   ├─ 📄 IceRushEffect.java
        │   │   │   │   │   │   ├─ 📄 SaradominStrikeEffect.java
        │   │   │   │   │   │   ├─ 📄 ShadowBarrageEffect.java
        │   │   │   │   │   │   ├─ 📄 ShadowBlitzEffect.java
        │   │   │   │   │   │   ├─ 📄 ShadowBurstEffect.java
        │   │   │   │   │   │   ├─ 📄 ShadowRushEffect.java
        │   │   │   │   │   │   ├─ 📄 SmokeBarrageEffect.java
        │   │   │   │   │   │   ├─ 📄 SmokeBlitzEffect.java
        │   │   │   │   │   │   ├─ 📄 SmokeBurstEffect.java
        │   │   │   │   │   │   ├─ 📄 SmokeRushEffect.java
        │   │   │   │   │   │   ├─ 📄 SnareEffect.java
        │   │   │   │   │   │   └─ 📄 TeleBlockEffect.java
        │   │   │   │   │   ├─ 📄 MageConstants.java
        │   │   │   │   │   ├─ 📄 MageEffects.java
        │   │   │   │   │   ├─ 📄 MageProfession.java
        │   │   │   │   │   ├─ 📄 Spell.java
        │   │   │   │   │   ├─ 📄 SpellBookTeleporting.java
        │   │   │   │   │   ├─ 📄 SpellCasting.java
        │   │   │   │   │   ├─ 📂 spells
        │   │   │   │   │   │   ├─ 📄 BoltEnchanting.java
        │   │   │   │   │   │   ├─ 📄 Charge.java
        │   │   │   │   │   │   ├─ 📄 HighAlchemy.java
        │   │   │   │   │   │   ├─ 📄 LowAlchemy.java
        │   │   │   │   │   │   └─ 📄 SuperHeat.java
        │   │   │   │   │   ├─ 📄 TabCreation.java
        │   │   │   │   │   └─ 📂 weapons
        │   │   │   │   │       ├─ 📄 TridentOfTheSeas.java
        │   │   │   │   │       └─ 📄 TridentOfTheSwamp.java
        │   │   │   │   ├─ 📂 melee
        │   │   │   │   │   ├─ 📄 BarrowsSpecials.java
        │   │   │   │   │   ├─ 📄 Melee.java
        │   │   │   │   │   └─ 📄 SerpentineHelmet.java
        │   │   │   │   ├─ 📂 mercenary
        │   │   │   │   │   ├─ 📄 Mercenary.java
        │   │   │   │   │   ├─ 📄 MercenaryMonsters.java
        │   │   │   │   │   └─ 📄 MercenaryTasks.java
        │   │   │   │   ├─ 📂 resonance
        │   │   │   │   │   ├─ 📄 PetInteraction.java
        │   │   │   │   │   └─ 📄 PetTrainer.java
        │   │   │   │   ├─ 📄 Profession.java
        │   │   │   │   ├─ 📄 ProfessionGoal.java
        │   │   │   │   ├─ 📄 Professions.java
        │   │   │   │   ├─ 📂 pyromaniac
        │   │   │   │   │   ├─ 📄 PyroAutoBurn.java
        │   │   │   │   │   └─ 📄 Pyromaniac.java
        │   │   │   │   ├─ 📂 quarrying
        │   │   │   │   │   └─ 📄 Quarrying.java
        │   │   │   │   ├─ 📂 sagittarius
        │   │   │   │   │   ├─ 📄 AmmoData.java
        │   │   │   │   │   ├─ 📄 BoltSpecials.java
        │   │   │   │   │   ├─ 📄 SagittariusConstants.java
        │   │   │   │   │   ├─ 📄 SagittariusProfession.java
        │   │   │   │   │   └─ 📄 ToxicBlowpipe.java
        │   │   │   │   ├─ 📂 summoning
        │   │   │   │   │   ├─ 📄 BOBContainer.java
        │   │   │   │   │   ├─ 📄 Familiar.java
        │   │   │   │   │   ├─ 📄 FamiliarMob.java
        │   │   │   │   │   ├─ 📄 FamiliarSpecial.java
        │   │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   │   ├─ 📄 Minotaur.java
        │   │   │   │   │   │   ├─ 📄 SpiritJelly.java
        │   │   │   │   │   │   ├─ 📄 SpiritSpider.java
        │   │   │   │   │   │   ├─ 📄 SpiritTerrorBird.java
        │   │   │   │   │   │   ├─ 📄 SpiritWolf.java
        │   │   │   │   │   │   └─ 📄 UnicornStallion.java
        │   │   │   │   │   ├─ 📄 Pouch.java
        │   │   │   │   │   ├─ 📄 Scroll.java
        │   │   │   │   │   ├─ 📄 Summoning.java
        │   │   │   │   │   ├─ 📄 SummoningConstants.java
        │   │   │   │   │   └─ 📄 SummoningCreation.java
        │   │   │   │   ├─ 📂 thchempistry
        │   │   │   │   │   ├─ 📄 CleanWeedTask.java
        │   │   │   │   │   ├─ 📄 FinishedPotionData.java
        │   │   │   │   │   ├─ 📄 GrindingData.java
        │   │   │   │   │   ├─ 📄 PotionDecanting.java
        │   │   │   │   │   ├─ 📄 SuperCombatPotion.java
        │   │   │   │   │   ├─ 📄 THChempistryFinishedPotionTask.java
        │   │   │   │   │   ├─ 📄 THChempistryGrindingTask.java
        │   │   │   │   │   ├─ 📄 THChempistryMasterProcessor.java
        │   │   │   │   │   ├─ 📄 THChempistryUnfinishedPotionTask.java
        │   │   │   │   │   ├─ 📄 UnfinishedPotionData.java
        │   │   │   │   │   └─ 📄 UntrimmedWeedData.java
        │   │   │   │   ├─ 📂 weedsmoking
        │   │   │   │   │   └─ 📄 Weedsmoker.java
        │   │   │   │   └─ 📂 woodcarving
        │   │   │   │       ├─ 📂 fletchable
        │   │   │   │       │   ├─ 📄 Fletchable.java
        │   │   │   │       │   ├─ 📄 FletchableItem.java
        │   │   │   │       │   └─ 📂 impl
        │   │   │   │       │       ├─ 📄 Arrow.java
        │   │   │   │       │       ├─ 📄 Bolt.java
        │   │   │   │       │       ├─ 📄 Carvable.java
        │   │   │   │       │       ├─ 📄 Crossbow.java
        │   │   │   │       │       ├─ 📄 Featherable.java
        │   │   │   │       │       └─ 📄 Stringable.java
        │   │   │   │       └─ 📄 Woodcarving.java
        │   │   │   ├─ 📄 ProfessionsChat.java
        │   │   │   ├─ 📂 profiles
        │   │   │   │   ├─ 📄 ProfileLeaderboard.java
        │   │   │   │   └─ 📄 StonerProfiler.java
        │   │   │   ├─ 📂 randomevent
        │   │   │   │   ├─ 📄 RandomEvent.java
        │   │   │   │   └─ 📄 RandomEventController.java
        │   │   │   ├─ 📄 RunEnergy.java
        │   │   │   ├─ 📂 shopping
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 AchievementShop.java
        │   │   │   │   │   ├─ 📄 AdvanceShop.java
        │   │   │   │   │   ├─ 📄 BountyShop.java
        │   │   │   │   │   ├─ 📄 CannaCreditsShop.java
        │   │   │   │   │   ├─ 📄 CannaCreditsShop2.java
        │   │   │   │   │   ├─ 📄 CannaCreditsShop3.java
        │   │   │   │   │   ├─ 📄 ChillShop.java
        │   │   │   │   │   ├─ 📄 ExerciseShop.java
        │   │   │   │   │   ├─ 📄 GracefulShop.java
        │   │   │   │   │   ├─ 📄 MageArenaShop.java
        │   │   │   │   │   ├─ 📄 MasterCapeShop.java
        │   │   │   │   │   ├─ 📄 MercenaryShop.java
        │   │   │   │   │   ├─ 📄 PestShop.java
        │   │   │   │   │   ├─ 📄 ProfessioncapeShop.java
        │   │   │   │   │   └─ 📄 TokkulShop.java
        │   │   │   │   ├─ 📄 Shop.java
        │   │   │   │   ├─ 📄 ShopConstants.java
        │   │   │   │   └─ 📄 Shopping.java
        │   │   │   ├─ 📂 sounds
        │   │   │   │   ├─ 📄 MobSounds.java
        │   │   │   │   ├─ 📄 MusicStoner.java
        │   │   │   │   └─ 📄 StonerSounds.java
        │   │   │   ├─ 📄 Spawns.java
        │   │   │   ├─ 📄 StarterKit.java
        │   │   │   ├─ 📄 StonerProfiler.java
        │   │   │   ├─ 📄 StonerProperties.java
        │   │   │   ├─ 📄 StonersOnline.java
        │   │   │   ├─ 📄 StonerTitle.java
        │   │   │   ├─ 📄 TeleportHandler.java
        │   │   │   ├─ 📂 trading
        │   │   │   │   ├─ 📄 Trade.java
        │   │   │   │   └─ 📄 TradeContainer.java
        │   │   │   ├─ 📂 wilderness
        │   │   │   │   ├─ 📄 BountyEmblems.java
        │   │   │   │   ├─ 📄 GainTarget.java
        │   │   │   │   ├─ 📄 Lockpick.java
        │   │   │   │   ├─ 📄 StonerKilling.java
        │   │   │   │   └─ 📄 TargetSystem.java
        │   │   │   └─ 📄 Yelling.java
        │   │   ├─ 📂 entity
        │   │   │   ├─ 📄 Animation.java
        │   │   │   ├─ 📄 Area.java
        │   │   │   ├─ 📄 Attributes.java
        │   │   │   ├─ 📄 Entity.java
        │   │   │   ├─ 📂 following
        │   │   │   │   ├─ 📄 Following.java
        │   │   │   │   ├─ 📄 MobFollowing.java
        │   │   │   │   └─ 📄 StonerFollowing.java
        │   │   │   ├─ 📄 Graphic.java
        │   │   │   ├─ 📄 InterfaceManager.java
        │   │   │   ├─ 📂 item
        │   │   │   │   ├─ 📄 BasicItemContainer.java
        │   │   │   │   ├─ 📄 Equipment.java
        │   │   │   │   ├─ 📄 EquipmentConstants.java
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 GlobalItemHandler.java
        │   │   │   │   │   ├─ 📄 GroundItem.java
        │   │   │   │   │   ├─ 📄 GroundItemHandler.java
        │   │   │   │   │   └─ 📄 LocalGroundItems.java
        │   │   │   │   ├─ 📄 Item.java
        │   │   │   │   ├─ 📄 ItemCheck.java
        │   │   │   │   ├─ 📄 ItemContainer.java
        │   │   │   │   ├─ 📄 ItemCreating.java
        │   │   │   │   └─ 📄 ItemDegrading.java
        │   │   │   ├─ 📄 Location.java
        │   │   │   ├─ 📂 mob
        │   │   │   │   ├─ 📂 abilities
        │   │   │   │   │   ├─ 📄 BarrelchestAbility.java
        │   │   │   │   │   ├─ 📄 BorkAbility.java
        │   │   │   │   │   ├─ 📄 CorporealBeastAbility.java
        │   │   │   │   │   ├─ 📄 HobgoblinGeomancerAbility.java
        │   │   │   │   │   ├─ 📄 IcyBonesAbility.java
        │   │   │   │   │   └─ 📄 JadAbility.java
        │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   ├─ 📄 CorporealBeast.java
        │   │   │   │   │   ├─ 📄 DarkEnergyCore.java
        │   │   │   │   │   ├─ 📄 GelatinnothMother.java
        │   │   │   │   │   ├─ 📄 GiantMole.java
        │   │   │   │   │   ├─ 📄 KalphiteQueen.java
        │   │   │   │   │   ├─ 📄 Kraken.java
        │   │   │   │   │   ├─ 📄 Kreearra.java
        │   │   │   │   │   ├─ 📄 SeaTrollQueen.java
        │   │   │   │   │   ├─ 📄 Tentacles.java
        │   │   │   │   │   ├─ 📂 wild
        │   │   │   │   │   │   ├─ 📄 Callisto.java
        │   │   │   │   │   │   ├─ 📄 ChaosElemental.java
        │   │   │   │   │   │   ├─ 📄 ChaosFanatic.java
        │   │   │   │   │   │   ├─ 📄 CrazyArchaeologist.java
        │   │   │   │   │   │   ├─ 📄 Scorpia.java
        │   │   │   │   │   │   ├─ 📄 Venenatis.java
        │   │   │   │   │   │   └─ 📄 Vetion.java
        │   │   │   │   │   └─ 📄 Zulrah.java
        │   │   │   │   ├─ 📄 Mob.java
        │   │   │   │   ├─ 📄 MobAbilities.java
        │   │   │   │   ├─ 📄 MobConstants.java
        │   │   │   │   ├─ 📄 MobDrops.java
        │   │   │   │   ├─ 📄 MobFollowDistance.java
        │   │   │   │   ├─ 📄 MobUpdateFlags.java
        │   │   │   │   ├─ 📄 RandomMobChatting.java
        │   │   │   │   ├─ 📄 RareDropEP.java
        │   │   │   │   ├─ 📄 RareDropTable.java
        │   │   │   │   ├─ 📄 VirtualMobRegion.java
        │   │   │   │   └─ 📄 Walking.java
        │   │   │   ├─ 📂 movement
        │   │   │   │   ├─ 📄 MobMovementHandler.java
        │   │   │   │   ├─ 📄 MovementHandler.java
        │   │   │   │   ├─ 📄 Point.java
        │   │   │   │   └─ 📄 StonerMovementHandler.java
        │   │   │   ├─ 📂 object
        │   │   │   │   ├─ 📄 GameObject.java
        │   │   │   │   ├─ 📄 LocalObjects.java
        │   │   │   │   ├─ 📄 ObjectConstants.java
        │   │   │   │   └─ 📄 ObjectManager.java
        │   │   │   ├─ 📄 ObjectActions.java
        │   │   │   ├─ 📄 Palette.java
        │   │   │   ├─ 📂 pathfinding
        │   │   │   │   ├─ 📄 RS317PathFinder.java
        │   │   │   │   ├─ 📄 SimplePathWalker.java
        │   │   │   │   └─ 📄 StraightPathFinder.java
        │   │   │   ├─ 📄 Projectile.java
        │   │   │   ├─ 📄 ReportHandler.java
        │   │   │   ├─ 📄 Sound.java
        │   │   │   ├─ 📂 stoner
        │   │   │   │   ├─ 📂 controllers
        │   │   │   │   │   ├─ 📄 Controller.java
        │   │   │   │   │   ├─ 📄 ControllerManager.java
        │   │   │   │   │   ├─ 📄 DefaultController.java
        │   │   │   │   │   ├─ 📄 GenericMinigameController.java
        │   │   │   │   │   ├─ 📄 GenericWaitingRoomController.java
        │   │   │   │   │   └─ 📄 WildernessController.java
        │   │   │   │   ├─ 📂 net
        │   │   │   │   │   ├─ 📄 Client.java
        │   │   │   │   │   ├─ 📂 in
        │   │   │   │   │   │   ├─ 📂 command
        │   │   │   │   │   │   │   ├─ 📄 Command.java
        │   │   │   │   │   │   │   ├─ 📄 CommandParser.java
        │   │   │   │   │   │   │   └─ 📂 impl
        │   │   │   │   │   │   │       ├─ 📄 AdministratorCommand.java
        │   │   │   │   │   │   │       ├─ 📄 DeveloperCommand.java
        │   │   │   │   │   │   │       ├─ 📄 Hit.java
        │   │   │   │   │   │   │       ├─ 📄 ModeratorCommand.java
        │   │   │   │   │   │   │       ├─ 📄 OwnerCommand.java
        │   │   │   │   │   │   │       └─ 📄 StonerCommand.java
        │   │   │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   │   │   ├─ 📄 BankAllButOne.java
        │   │   │   │   │   │   │   ├─ 📄 BankModifiableX.java
        │   │   │   │   │   │   │   ├─ 📄 ChangeAppearancePacket.java
        │   │   │   │   │   │   │   ├─ 📄 ChangeRegionPacket.java
        │   │   │   │   │   │   │   ├─ 📄 ChatBridgeManager.java
        │   │   │   │   │   │   │   ├─ 📄 ChatInterfacePacket.java
        │   │   │   │   │   │   │   ├─ 📂 clickbuttons
        │   │   │   │   │   │   │   │   ├─ 📄 ButtonAssignment.java
        │   │   │   │   │   │   │   │   └─ 📄 ClickButtonPacket.java
        │   │   │   │   │   │   │   ├─ 📄 CloseInterfacePacket.java
        │   │   │   │   │   │   │   ├─ 📄 CommandPacket.java
        │   │   │   │   │   │   │   ├─ 📄 DockPanelPacket.java
        │   │   │   │   │   │   │   ├─ 📄 FlashingSideIconPacket.java
        │   │   │   │   │   │   │   ├─ 📄 InputFieldPacket.java
        │   │   │   │   │   │   │   ├─ 📄 InterfaceAction.java
        │   │   │   │   │   │   │   ├─ 📄 ItemPackets.java
        │   │   │   │   │   │   │   ├─ 📄 JoinChat.java
        │   │   │   │   │   │   │   ├─ 📄 MovementPacket.java
        │   │   │   │   │   │   │   ├─ 📄 NPCPacket.java
        │   │   │   │   │   │   │   ├─ 📄 ObjectPacket.java
        │   │   │   │   │   │   │   ├─ 📄 PrivateMessagingPacket.java
        │   │   │   │   │   │   │   ├─ 📄 PublicChatPacket.java
        │   │   │   │   │   │   │   ├─ 📄 ReceiveString.java
        │   │   │   │   │   │   │   ├─ 📄 ResetCounter.java
        │   │   │   │   │   │   │   ├─ 📄 StonerOptionPacket.java
        │   │   │   │   │   │   │   └─ 📄 StringInputPacket.java
        │   │   │   │   │   │   ├─ 📄 IncomingPacket.java
        │   │   │   │   │   │   └─ 📄 PacketHandler.java
        │   │   │   │   │   ├─ 📄 NPCUpdating.java
        │   │   │   │   │   ├─ 📂 out
        │   │   │   │   │   │   ├─ 📂 impl
        │   │   │   │   │   │   │   ├─ 📄 ConstructMap.java
        │   │   │   │   │   │   │   ├─ 📄 SendAltConfig.java
        │   │   │   │   │   │   │   ├─ 📄 SendAltCoordinates.java
        │   │   │   │   │   │   │   ├─ 📄 SendAnimateObject.java
        │   │   │   │   │   │   │   ├─ 📄 SendBanner.java
        │   │   │   │   │   │   │   ├─ 📄 SendBox.java
        │   │   │   │   │   │   │   ├─ 📄 SendBoxInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendCharacterDetail.java
        │   │   │   │   │   │   │   ├─ 📄 SendChatBoxInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendClanChatUpdate.java
        │   │   │   │   │   │   │   ├─ 📄 SendClanMessage.java
        │   │   │   │   │   │   │   ├─ 📄 SendColor.java
        │   │   │   │   │   │   │   ├─ 📄 SendConfig.java
        │   │   │   │   │   │   │   ├─ 📄 SendCoordinates.java
        │   │   │   │   │   │   │   ├─ 📄 SendDetails.java
        │   │   │   │   │   │   │   ├─ 📄 SendDuelEquipment.java
        │   │   │   │   │   │   │   ├─ 📄 SendEnergy.java
        │   │   │   │   │   │   │   ├─ 📄 SendEnterString.java
        │   │   │   │   │   │   │   ├─ 📄 SendEnterXInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendEquipment.java
        │   │   │   │   │   │   │   ├─ 📄 SendExpCounter.java
        │   │   │   │   │   │   │   ├─ 📄 SendFlashSidebarIcon.java
        │   │   │   │   │   │   │   ├─ 📄 SendForgingItem.java
        │   │   │   │   │   │   │   ├─ 📄 SendFriendUpdate.java
        │   │   │   │   │   │   │   ├─ 📄 SendGameUpdateTimer.java
        │   │   │   │   │   │   │   ├─ 📄 SendGlobalSound.java
        │   │   │   │   │   │   │   ├─ 📄 SendGroundItem.java
        │   │   │   │   │   │   │   ├─ 📄 SendInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendInterfaceConfig.java
        │   │   │   │   │   │   │   ├─ 📄 SendItemOnInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendLoginResponse.java
        │   │   │   │   │   │   │   ├─ 📄 SendLogout.java
        │   │   │   │   │   │   │   ├─ 📄 SendMapRegion.java
        │   │   │   │   │   │   │   ├─ 📄 SendMapState.java
        │   │   │   │   │   │   │   ├─ 📄 SendMessage.java
        │   │   │   │   │   │   │   ├─ 📄 SendModelAnimation.java
        │   │   │   │   │   │   │   ├─ 📄 SendMoveCamera.java
        │   │   │   │   │   │   │   ├─ 📄 SendMoveComponent.java
        │   │   │   │   │   │   │   ├─ 📄 SendMultiInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendNPCDialogueHead.java
        │   │   │   │   │   │   │   ├─ 📄 SendNpcDisplay.java
        │   │   │   │   │   │   │   ├─ 📄 SendNPCUpdate.java
        │   │   │   │   │   │   │   ├─ 📄 SendObject.java
        │   │   │   │   │   │   │   ├─ 📄 SendObjectHint.java
        │   │   │   │   │   │   │   ├─ 📄 SendOpenTab.java
        │   │   │   │   │   │   │   ├─ 📄 SendPMServer.java
        │   │   │   │   │   │   │   ├─ 📄 SendPrivateMessage.java
        │   │   │   │   │   │   │   ├─ 📄 SendProfession.java
        │   │   │   │   │   │   │   ├─ 📄 SendProfessionGoal.java
        │   │   │   │   │   │   │   ├─ 📄 SendProjectile.java
        │   │   │   │   │   │   │   ├─ 📄 SendQuickSong.java
        │   │   │   │   │   │   │   ├─ 📄 SendRemoveGroundItem.java
        │   │   │   │   │   │   │   ├─ 📄 SendRemoveInterfaces.java
        │   │   │   │   │   │   │   ├─ 📄 SendResetCamera.java
        │   │   │   │   │   │   │   ├─ 📄 SendScrollInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendShakeScreen.java
        │   │   │   │   │   │   │   ├─ 📄 SendSidebarInterface.java
        │   │   │   │   │   │   │   ├─ 📄 SendSong.java
        │   │   │   │   │   │   │   ├─ 📄 SendSound.java
        │   │   │   │   │   │   │   ├─ 📄 SendSpecialBar.java
        │   │   │   │   │   │   │   ├─ 📄 SendStillCamera.java
        │   │   │   │   │   │   │   ├─ 📄 SendStillGraphic.java
        │   │   │   │   │   │   │   ├─ 📄 SendStonerDialogueHead.java
        │   │   │   │   │   │   │   ├─ 📄 SendStonerHint.java
        │   │   │   │   │   │   │   ├─ 📄 SendStonerOption.java
        │   │   │   │   │   │   │   ├─ 📄 SendStonerProfilerIndex.java
        │   │   │   │   │   │   │   ├─ 📄 SendStonerUpdate.java
        │   │   │   │   │   │   │   ├─ 📄 SendString.java
        │   │   │   │   │   │   │   ├─ 📄 SendSystemBan.java
        │   │   │   │   │   │   │   ├─ 📄 SendTurnCamera.java
        │   │   │   │   │   │   │   ├─ 📄 SendUpdateEnergy.java
        │   │   │   │   │   │   │   ├─ 📄 SendUpdateFlashingSidebarIcon.java
        │   │   │   │   │   │   │   ├─ 📄 SendUpdateItems.java
        │   │   │   │   │   │   │   ├─ 📄 SendUpdateItemsAlt.java
        │   │   │   │   │   │   │   ├─ 📄 SendUpdateSpecialBar.java
        │   │   │   │   │   │   │   ├─ 📄 SendWalkableInterface.java
        │   │   │   │   │   │   │   └─ 📄 SendWeight.java
        │   │   │   │   │   │   ├─ 📄 impl.zip
        │   │   │   │   │   │   └─ 📄 OutgoingPacket.java
        │   │   │   │   │   └─ 📄 StonerUpdating.java
        │   │   │   │   ├─ 📄 Stoner.java
        │   │   │   │   ├─ 📄 StonerAnimations.java
        │   │   │   │   ├─ 📄 StonerAssistant.java
        │   │   │   │   ├─ 📄 StonerConstants.java
        │   │   │   │   └─ 📄 StonerUpdateFlags.java
        │   │   │   ├─ 📄 UpdateFlags.java
        │   │   │   └─ 📄 World.java
        │   │   ├─ 📄 GameConstants.java
        │   │   └─ 📂 util
        │   │       └─ 📄 Cooldown.java
        │   ├─ 📄 Server.java
```