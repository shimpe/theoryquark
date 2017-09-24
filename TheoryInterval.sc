TheoryInterval {
	var <note1;
	var <note2;

	classvar >parser;
	classvar <>idx_to_notenames;
	classvar <>notenames_to_idx;
	classvar <>interval_dict;

	*new {
		| note1, note2 |
		^super.new.init(note1,note2);
	}

	*initClass {
		Class.initClassTree(TheoryNoteParser);
		this.parser = TheoryNoteParser.new;
		this.idx_to_notenames = ["a", "b", "c", "d", "e", "f", "g"];
		this.notenames_to_idx = Dictionary.new();
		idx_to_notenames.do({
			| note, idx |
			this.notenames_to_idx[note] = idx;
		});
		this.interval_dict = Dictionary.new;
		parser.chromatic_scale.do({
			| notelist, idx |
			notelist.do({
				| note |
				interval_dict[note] = idx;
			});
		});
	}

	init {
		| n1, n2 |
		var notenum = 0;
		note1 = n1;
		note2 = n2;
	}

	printOn {
		arg stream;
		stream << "TheoryInterval(\"" << note1 << "\", \"" << note2 << "\")";
	}

	== {
		| other |
		^(note1 == other.note1 && note2 == other.note2);
	}

	noteNameDistance {
		var n10 = note1[0];
		var n20 = note2[0];
		var n2i = notenames_to_idx;
		var idx_n1 = notenames_to_idx[n10.asString];
		var idx_n2 = notenames_to_idx[n20.asString];
		if ((idx_n1 > idx_n2), {
			^(idx_n2 + 7 - idx_n1);
		}, {
			^(idx_n2 - idx_n1);
		});
	}

	chromaticDistance {
		var n1 = parser.asMidi(note1)[0];
		var n2 = parser.asMidi(note2)[0];
		^(n2-n1);
	}

	transposeTo {
		| newnote |
		var new_note_idx = notenames_to_idx[note2[0].asString] + TheoryInterval.new(note1, newnote).noteNameDistance;
		var modifiers = (0:"", 1:"#", 2:"x", 11:"b", 10:"bb");
		var second_note;
		var key;
		var new_2ndnote;
		var try_distance;
		var expected_distance;
		var octave_diff;
		if ((new_note_idx > 6), {
			new_note_idx = new_note_idx - 7;
		});
		second_note = idx_to_notenames[new_note_idx];
		key = (TheoryInterval.new(note1, note2).chromaticDistance - TheoryInterval.new(newnote, second_note ++ "4").chromaticDistance).mod(12);
		new_2ndnote = second_note ++ modifiers[key];
		try_distance = TheoryInterval.new(newnote, new_2ndnote ++ "0").chromaticDistance;
		expected_distance = TheoryInterval.new(note1, note2).chromaticDistance;
		octave_diff = (expected_distance - try_distance).div(12);
		^TheoryInterval.new(newnote, new_2ndnote ++ octave_diff.asString);
	}
}