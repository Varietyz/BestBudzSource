package com.bestbudz.rs2.entity.stoner.net.in.impl;

import com.bestbudz.core.network.StreamBuffer;
import com.bestbudz.core.util.Utility;
import com.bestbudz.rs2.content.clanchat.Clan;
import com.bestbudz.rs2.entity.stoner.Stoner;
import com.bestbudz.rs2.entity.stoner.net.in.IncomingPacket;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendInterface;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendMessage;
import com.bestbudz.rs2.entity.stoner.net.out.impl.SendString;

public class InterfaceAction extends IncomingPacket {

	@Override
	public void handle(Stoner stoner, StreamBuffer.InBuffer in, int opcode, int length) {
	int id = in.readShort(false);
	int action = in.readShort(false);
	switch (id) {
	case 43704:
		if (action == 1) {
			stoner.getClan().delete();
			stoner.setClanData();
		}
		break;
	case 43707:
	case 43710:
	case 43713:
	case 43716:
		Clan clan = stoner.getClan();
		if (clan != null) {
			if (id == 43707) {
				clan.setRankCanJoin(action == 0 ? -1 : action);
			} else if (id == 43710) {
				clan.setRankCanTalk(action == 0 ? -1 : action);
			} else if (id == 43713) {
				clan.setRankCanKick(action == 0 ? -1 : action);
			} else if (id == 43716) {
				clan.setRankCanBan(action == 0 ? -1 : action);
			}
			String title = "";
			if (id == 43707) {
				title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 43710) {
				title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 43713) {
				title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 43716) {
				title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
			}
			stoner.send(new SendString(title, id + 2));
		}
		break;

	default:

		break;
	}
	if (id >= 43723 && id < 43823) {
		Clan clan = stoner.getClan();
		if (clan != null && clan.rankedMembers != null && !clan.rankedMembers.isEmpty()) {
			String member = clan.rankedMembers.get(id - 43723);
			switch (action) {
			case 0:
				clan.demote(member);
				break;
			default:
				clan.setRank(member, action);
				break;
			}
			stoner.setClanData();
		}
	}
	if (id >= 43824 && id < 43924) {
		Clan clan = stoner.getClan();
		if (clan != null && clan.bannedMembers != null && !clan.bannedMembers.isEmpty()) {
			String member = clan.bannedMembers.get(id - 43824);
			switch (action) {
			case 0:
				clan.unbanMember(member);
				break;
			}
			stoner.setClanData();
		}
	}
	if (id >= 18144 && id < 18244) {
		for (int index = 0; index < 100; index++) {
			if (id == index + 18144) {
				String member = stoner.clan.activeMembers.get(id - 18144);
				if (member == null) {
					return;
				}
				switch (action) {
				case 0:
					if (stoner.clan.isFounder(stoner.getUsername()) && !stoner.getCombat().inCombat()) {
						stoner.send(new SendInterface(43700));
					}
					break;
				case 1:
					if (member.equalsIgnoreCase(stoner.getUsername())) {
						stoner.send(new SendMessage("You can't kick yourself!"));
					} else {
						if (stoner.clan.canKick(stoner.getUsername())) {
							stoner.clan.kickMember(member);
						} else {
							stoner.send(new SendMessage("You do not have sufficient privileges to do this."));
						}
					}
					break;
				case 2:
					if (member.length() == 0) {
						break;
					} else if (member.length() > 12) {
						member = member.substring(0, 12);
					}
					if (member.equalsIgnoreCase(stoner.getUsername())) {
						break;
					}
					Clan clan = stoner.getClan();
					if (clan.isRanked(member) || clan.isAdmin(member)) {
						stoner.send(new SendMessage("You can't ban a ranked member of this channel."));
						break;
					}
					if (clan != null) {
						clan.banMember(Utility.formatStonerName(member));
						stoner.setClanData();
						clan.save();
					}
					break;
				}
				break;
			}
		}
	}
	}

	@Override
	public int getMaxDuplicates() {
	return 1;
	}
}