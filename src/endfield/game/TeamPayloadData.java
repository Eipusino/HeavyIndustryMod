package endfield.game;

import arc.struct.Seq;
import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.io.ICustomChunk;
import endfield.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.type.PayloadSeq;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TeamPayloadData implements ICustomChunk {
	public CollectionObjectMap<Team, PayloadSeq> teamPayloadData = new CollectionObjectMap<>(Team.class, PayloadSeq.class);

	public TeamPayloadData() {}

	public void addPayload(Team team, UnlockableContent content, int count) {
		PayloadSeq payload = getPayload(team);
		payload.add(content, count);
		teamPayloadData.put(team, payload);
	}

	public void removePayload(Team team, UnlockableContent content, int count) {
		PayloadSeq payload = getPayload(team);
		if (payload.get(content) < count) return;
		payload.remove(content, count);
		teamPayloadData.remove(team);
	}

	public PayloadSeq getPayload(Team team) {
		PayloadSeq payload = teamPayloadData.get(team);
		if (payload == null) {
			payload = new PayloadSeq();
			teamPayloadData.put(team, payload);
		}
		return payload;
	}

	public void display() {
		StringBuilder builder = new StringBuilder();
		builder.append("Team Payload Data\n");
		for (var entry : teamPayloadData.iterator()) {
			Team team = entry.key;
			PayloadSeq payload = entry.value;

			builder.append("-----").append(team.name).append("-----").append('\n');
			for (Seq<Content> seq : Vars.content.getContentMap()) {
				for (Content content : seq) {
					if (content instanceof UnlockableContent uc) {
						if (payload.get(uc) != 0) {
							builder.append(uc.name).append(' ').append(payload.get(uc)).append(' ').append('\n');
						}
					}
				}
			}
		}
		Log.info(builder.toString());
	}

	@Override
	public void write(DataOutput stream) throws IOException {
		try (Writes write = new Writes(stream)) {
			write.b(teamPayloadData.size);

			for (var entry : teamPayloadData.iterator()) {
				Team team = entry.key;
				PayloadSeq payloads = entry.value;

				write.b(team.id);
				payloads.write(write);
			}
		}
	}

	@Override
	public void read(DataInput stream) throws IOException {
		teamPayloadData.clear();
		try (Reads read = new Reads(stream)) {
			int size = read.b();
			for (int i = 0; i < size; i++) {
				Team team = Team.get(read.b());
				PayloadSeq payloads = new PayloadSeq();
				payloads.read(read);
				teamPayloadData.put(team, payloads);
			}
		}
	}

	@Override
	public void read(DataInput stream, int length) throws IOException {
		read(stream);
	}
}
