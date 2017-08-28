TheoryScale {
	var <>referencenote;
	var <>name;
    var <>notes;
	var <>midi_to_degree;

	classvar >parser;
	classvar >steps_per_octave;

	*new {
		| basenote, type, collection|
		^super.new.init(basenote, type, collection);
	}

	*initClass {
		Class.initClassTree(TheoryNoteParser);
		this.parser = TheoryNoteParser.new;
		this.steps_per_octave = 12;
	}

	init {
		| basenote, type, collection |
		var m;
		referencenote = basenote;
		name = type;
		notes = collection;
		midi_to_degree = Dictionary.new;
		m = this.asMidi.value;
		m.do({
			| midi, idx |
			while ({midi >= steps_per_octave }, { midi = midi-steps_per_octave});
			midi_to_degree[midi] = idx;
		});
	}

	printOn {
		arg stream;
		stream << "TheoryScale(\"" << referencenote << "\", \"" << name << "\"" << ", \"" << notes << "\")";
	}

	asMidi {
		if (notes.isKindOf(String), { ^parser.asMidi(notes) });
		if (notes[0].isKindOf(SimpleNumber),{ ^notes });
		^notes.collect({ | note | parser.asMidi(note); })[0];
	}

	asDegree {
		^midi_to_degree;
	}

	noteToDegree {
		| note |
		var midi = parser.asMidi(note)[0];
		^this.midiToDegree(midi);
	}

	midiToDegree {
		| midi |
		var ub = 10000, lb = 0;
		var min = midi_to_degree.minItem;
		var max = midi_to_degree.maxItem;
		while ({midi >= steps_per_octave }, { midi = midi - steps_per_octave});
		// handle degrees for notes inside scale
		if (midi_to_degree.keys.includes(midi), { ^midi_to_degree[midi]; });
		// handle degrees for notes outside scale
		midi_to_degree.keys.do({
			| m |
			if (((m > lb) && (m < midi)), { lb = m; });
			if (((m < ub) && (m > midi)), { ub = m; });
		});
		^midi.linlin(lb,ub,midi_to_degree[lb],midi_to_degree[ub]);
	}

	degreeToMidi {
		| degree, octave |
		var degree_to_midi = midi_to_degree.invert;
		var extra_octaves = 0, ub = 10000, lb = 0;
		while ({degree > degree_to_midi.keys.maxItem},{ degree = degree - degree_to_midi.keys.maxItem; extra_octaves = extra_octaves+1; });
		while ({degree < degree_to_midi.keys.minItem},{ degree = degree + degree_to_midi.keys.minItem; extra_octaves = extra_octaves-1; });
		// handle midi for notes inside scale
		if (degree_to_midi.keys.includes(degree), { ^(degree_to_midi[degree] + ((octave+extra_octaves+1)*steps_per_octave))});
		// handle midi for notes outside scale
		degree_to_midi.keys.do({
			| d |
			if (((d > lb) && (d < degree)), { lb = d; });
			if (((d < ub) && (d > degree)), { ub = d; });
		});
		^degree.linlin(lb,ub,degree_to_midi[lb] + ((octave+extra_octaves+1)*steps_per_octave),degree_to_midi[ub] + ((octave+extra_octaves+1)*steps_per_octave));
	}
}