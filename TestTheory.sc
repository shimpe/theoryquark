TestTheoryNote : UnitTest {
	test_asMidi {
		var b = TheoryNote.new("a4");
		this.assert(b.asMidi == 69, "a4 is correctly recognized");
	}
}

TestTheoryNoteParser : UnitTest {
	test_asMidi {
		var a = TheoryNoteParser.new;
		this.assert(a.asMidi("a4 c#5 e5 a5 bb5 e6 ax6") == [69, 73, 76, 81, 82, 88, 95], "Parse from string");
		this.assert(a.asMidi("a4 | c#5| e5|a5|bb5| e6|a6   | ", $|) == [69, 73, 76, 81, 82, 88, 93], "Parse from string with custom separator");
		this.assert(a.asMidi(["a4", "c#5", "e5", "a5", "bb5", "e6","a6"]) == [69, 73, 76, 81, 82, 88, 93], "Parse from Collection");
		this.assert(a.asMidi("c4 bx3 c#4 db4 dbb3 cb4 b3 f12") == [60, 61, 61, 61, 48, 59, 59, nil], "Parse octave corner cases");
	}
}

TestTheoryScale : UnitTest {
	test_asMidi {
		var a = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var b = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4".split());
		var c = TheoryScale.new("c4", "major", (60..71));
		this.assert(a.asMidi == [ 60, 62, 64, 65, 67, 69, 71 ], "Parse from string");
		this.assert(b.asMidi == [ 60, 62, 64, 65, 67, 69, 71 ], "Parse from collection");
		this.assert(c.asMidi == [ 60, 61, 62, 63,64,65,66,67,68,69,70,71], "Parse from midinumbers");
	}

	test_noteToDegree {
		var t, u;
		t = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		this.assert(t.noteToDegree("a4") == 5, "Note in scale");
		this.assert(t.noteToDegree("a#3") == 5.5, "Note outside scale and different octave");
		this.assert(t.noteToDegree("eb2") == 1.5, "Note outside scale and different octave 2");

		u = TheoryScale.new("c4", "minor", "c4 d4 eb4 f4 g4 ab4 bb4");
		this.assert(u.noteToDegree("a4") == 5.5, "Note outside scale 2");
		this.assert(u.noteToDegree("a#3") == 6, "Note in scale, different octave (kinda ;) ) 2");
		this.assert(u.noteToDegree("eb2") == 2, "Note in scale, different octave 2");
	}

	test_midiToDegree {
		var u;
		u = TheoryScale.new("e4", "major", "e4 f#4 g#4 a4 b4 c#5 d#5");
		this.assert(u.midiToDegree(69) == 3, "Note in scale");
		this.assert(u.midiToDegree(70) == 3.5, "Note outside scale");
		this.assert(u.midiToDegree(68.5) == 2.5, "Note outside scale 2");
		this.assert(u.midiToDegree(68) == 2, "Note outside scale 3");
		this.assert(u.midiToDegree(75) == 6, "Note at extreme of scale");
		this.assert(u.midiToDegree(75.5) == 6.5, "Note outside extreme of scale");
		this.assert(u.midiToDegree(0.5) == 4.75, "Note outside extreme of scale 2"); // 0.5 midi is c quarter sharp => in e major = 4.75
	}

	test_degreeToMidi {
		var u = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var v = TheoryScale.new("b4", "major", "b4 c#5 d#5 e5 f#5 g#5 a#5");

		this.assert(u.degreeToMidi(5, 4) == 69, "Degree in scale");
		this.assert(u.degreeToMidi((5+7), 4) == (69+12), "Degree > max degree in scale");
		this.assert(u.degreeToMidi(5.5, 4) == 70, "Degree outside scale");
		this.assert(u.degreeToMidi(5+7+0.5, 4) == (69+12+ 1), "Degree outside scale outside octave");
		this.assert(v.degreeToMidi(0, 4) == 71, "Degree in scale");
		this.assert(v.degreeToMidi(1, 4) == 73, "Degree in scale, crossing octave boundary");
	}

	test_midiToDegreeNotNorm {
		var u = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var v = TheoryScale.new("c4", "minor", "c4 d4 eb4 f4 g4 ab4 bb4");
		var n = TheoryNoteParser.new;
		this.assert(u.midiToDegree(69) == 5, "degree to Midi normalized");
		this.assert(u.midiToDegreeNotNorm(69) == 65, "degree to Midi not normalized");
		this.assert(v.midiToDegreeNotNorm(n.asMidi("bb4")[0]) == 66, "midi to degree inside scale");
		this.assert(v.midiToDegreeNotNorm(n.asMidi("b4")[0].postln) == 66.5, "midi to degree outside scale");

	}

	test_degreeNotNormToMidi {
		var u = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var v = TheoryScale.new("c4", "minor", "c4 d4 eb4 f4 g4 ab4 bb4");
		var n = TheoryNoteParser.new;
		this.assert(u.degreeNotNormToMidi(65) == 69, "degree not normalized to midi 1");
		this.assert(u.degreeNotNormToMidi(53) == 57, "degree not normalized to midi 2");
		this.assert(u.degreeNotNormToMidi([ 60, 53.5]) == [60, 58], "degree not normalized list to midi");
		this.assert(v.degreeNotNormToMidi(65.5).postln == n.asMidi("a4")[0].postln, "degree not normalized outside scale");
		["a3", "a#3", "b3", "c4", "c#4", "d4", "d#4", "e4", "f4", "f#4", "g4", "g#4", "a4"].do({
			| note |
			var midi;
			var deg;
			var m2d;
			var d2m;
			//"midi:".postln;
			midi = n.asMidi(note)[0].postln;
			//"deg-norm:".postln;
			//deg = v.midiToDegree(midi).postln;
			//"d2m-norm:".postln;
			//v.degreeToMidi(deg, 3).postln;
			//"m2d-notnorm:".postln;
			m2d = v.midiToDegreeNotNorm(midi).postln;
			//"d2m-notnorm:".postln;
			d2m = v.degreeNotNormToMidi(m2d).postln;
			this.assert(midi == d2m, "midi to degree not norm inverts degree not norm to midi "++note);
		});
	}

	test_symmetry1 {
		var u = TheoryScale.new("c4", "phrygian", "c4 db4 eb4 f4 g4 ab4 bb4");
		100.do({
			| i |
			this.assert((u.degreeToMidi(u.midiToDegree(i /11), -1) - (i / 11)) < 1e-5, i/11);
		});
	}

	test_symmetry2 {
		var u = TheoryScale.new("c4", "phrygian", "c4 db4 eb4 f4 g4 ab4 bb4");
		100.do({
			| i |
			this.assert((u.midiToDegree(u.degreeToMidi(i/11, 0)) - (i/11)) < 1e-5, i/11);
		});
	}

	test_modaltransposition {
		var u = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var v = TheoryScale.new("c4", "natural minor", "c4 d4 eb4 f4 g4 ab4 bb4");
		var tnp = TheoryNoteParser.new;
		this.assert(tnp.asMidi("c4 d4 e4 c4 c4 d4 e4 c4") == #[60, 62, 64, 60, 60, 62, 64, 60]);
		this.assert(u.midiToDegree(tnp.asMidi("c4 d4 e4 c4 c4 d4 e4 c4")) == #[0, 1, 2, 0, 0, 1, 2, 0], "midiToDegree");
		this.assert(v.degreeToMidi(u.midiToDegree(tnp.asMidi("c4 d4 e4 c4 c4 d4 e4 c4")), 4) == #[60, 62, 63, 60, 60, 62, 63, 60], "modal transposition");
	}
}

TestTheoryInterval : UnitTest {

	test_equality {
		var t = TheoryInterval.new("a4", "d5");
		var u = TheoryInterval.new("a4", "d5");
		this.assert(t == u, "equality of two intervals");
	}

	test_notenamedistance {
		var t = TheoryInterval.new("a4", "d5");
		var u = TheoryInterval.new("d5", "a4");
		this.assert(t.noteNameDistance == 3, "note name distance, crossing octave");
		this.assert(u.noteNameDistance == 4, "note name distance, normal");
	}

	test_chromaticdistance {
		var t = TheoryInterval.new("a4", "d5");
		var u = TheoryInterval.new("d5", "a4");
		this.assert(t.chromaticDistance == 5, "chromatic distance rising interval");
		this.assert(u.chromaticDistance == -5, "chromatic distance descending interval");
	}

	test_transposeto {
		var t = TheoryInterval.new("a4", "d5");
		var u = TheoryInterval.new("g4", "b4");
		var v = TheoryInterval.new("d5", "g4");
		this.assert(t.transposeTo("c#4") == TheoryInterval.new("c#4", "f#4"), "transpose up, from cross octave to intra octave");
		this.assert(u.transposeTo("b4") == TheoryInterval.new("b4", "d#5"), "transpose up, from intra octave to cross octave");
		this.assert(u.transposeTo("fx4") == TheoryInterval.new("fx4", "ax4"), "transpose to same note");
		this.assert(v.transposeTo("bb4") == TheoryInterval.new("bb4", "eb4"), "descending interval with flats");
	}
}

