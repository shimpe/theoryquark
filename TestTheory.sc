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
		u.midiToDegree(0.5);
		this.assert(u.midiToDegree(0.5) == 0.5, "Note outside extreme of scale 2");
	}

	test_degreeToMidi {
		var u = TheoryScale.new("c4", "major", "c4 d4 e4 f4 g4 a4 b4");
		var v = TheoryScale.new("b4", "major", "b4 c#5 d#5 e5 f#5 g#5 a#5");

		this.assert(u.degreeToMidi(5, 4) == 69, "Degree in scale");
		this.assert(u.degreeToMidi((5+6), 4) == (69+12), "Degree > max degree in scale");
		this.assert(u.degreeToMidi(5.5, 4) == 70, "Degree outside scale");
		this.assert(u.degreeToMidi(11.5, 4) == (69+12+1), "Degree outside scale outside octave");

		this.assert(v.degreeToMidi(0, 4) == 71, "Degree in scale");
		this.assert(v.degreeToMidi(1, 4) == 73, "Degree in scale, crossing octave boundary");
	}
}

