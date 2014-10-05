// A service that an agent may be capable of providing, optionally dependent on another capability
class Capability (val dependsOn: Option[Capability])
