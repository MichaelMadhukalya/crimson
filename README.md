### Crimson
Crimson is a simple, fast, light-weight, extensible JSON parser written in Java from scratch. It provides full **interface** compatibility with standard Java JSON APIs. Crimson is thread-safe; and inerops freely with data structures such as: *List*, *Map*, *String* etc. It uses *UTF-8* as the default encoding scheme in order to serialize raw bytes for persistent storage on dis).

### Design
Crimson uses a **recursive descent** strategy to parse inputs produced by lexical analyzer. A syntax checker validates input tokens in first pass looking for obvious issues such as: incorrect parenthesis matching etc. On the other hand, semantic verification guards against issues such as key names not being in proper format e.g. *"key1"* as opposed to being *2E-05* or *null*. Finally, a recursive descent parsing stage de-serializes provided input into one of the 6 supported data types. We discuss more about the hierarcy of data types supported by Crimson in the following sections. 
