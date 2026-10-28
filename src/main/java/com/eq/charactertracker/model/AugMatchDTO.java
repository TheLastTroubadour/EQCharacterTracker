package com.eq.charactertracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AugMatchDTO {
    private CharacterDTO character;
    // The specific equipped aug name(s) that matched the search -- a character can match more
    // than once (e.g. the same aug in two slots, or two different augs whose names both contain
    // the search text), so this is a distinct set of matching names rather than a single name.
    private List<String> augNames;
}
