
TheoryNote {
	var <name;

	classvar >parser;

	*new {
		arg n;
		^super.new.init(n);
	}

	*initClass {
		Class.initClassTree(TheoryNoteParser);
		this.parser = TheoryNoteParser.new;
	}

	init {
		arg n;
		var notenum = 0;
		name = n;
	}

	printOn {
		arg stream;
		stream << "TheoryNote(\"" << name << "\")";
	}

	asMidi {
		^parser.asMidi(name)[0];
	}
}

