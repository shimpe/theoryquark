TheoryNoteParser {
	var <chromatic_scale;
	var <lookup_table;

	*new {
		^super.new.init();
	}

	init {
		var notenum = 0;
		var corner_case_octave_lower;
		var corner_case_octave_higher;
		lookup_table = Dictionary.new;

		chromatic_scale = [["c", "b#", "b+", "dbb", "d--"],  // one row contains all synonyms (i.e. synonym for our purpose)
			["c#", "c+", "bx", "b++", "db", "d-"],
			["d", "cx", "c++", "ebb", "e--"],
			["d#", "d+", "eb", "e-", "fbb", "f--"],
			["e", "dx", "d++", "fb", "f-"],
			["f", "e#", "e+", "gbb", "g--"],
			["f#", "f+", "ex", "e++", "gb", "g-"],
			["g", "fx", "f++", "abb", "a--"],
			["g#", "g+", "ab", "a-"],
			["a", "gx", "g++",  "bbb", "b--"],
			["a#", "a+", "bb", "b-", "cbb", "c--"],
			["b", "ax", "a++", "cb", "c-"]];
		corner_case_octave_lower = Set["b#", "bx", "b+", "b++"];
		corner_case_octave_higher = Set["cb", "c-", "cbb", "c--"];

		11.do({
			| octave |
			chromatic_scale.do({
				| synonyms |
				synonyms.do({
					| note |
					var o = octave - 1;
					if (corner_case_octave_lower.includes(note),{
						o = o - 1;
					}, {
						if (corner_case_octave_higher.includes(note), {
							o = o + 1
						})
					});
					lookup_table[note++o.asString] = notenum;
				});
				notenum = notenum+1;
			});
		});
	}

	asMidi {
		| notenames, separator=$\ |

		if (notenames.isKindOf(String), {
			^notenames.stripWhiteSpace.split(separator).reject({|i| i.size<1}).collect({ | note | lookup_table[note.stripWhiteSpace]; })
		});

		if (notenames.isKindOf(Collection), {
			^notenames.collect({ | note | lookup_table[note.stripWhiteSpace] });

		});
	}

	printOn {
		arg stream;
		stream << "TheoryNoteParser()";
	}
}
