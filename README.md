# BrickOne

We have started building our own [DynamicLand](https://dynamicland.org/)- / [FolkComputer](https://folk.computer/)-inspired communal computer -- where the physical space around us gains new interactive abilities to help us think, create, and play together. A "Room OS" to start with, and eventually, a "Tangible OS" which you can take anywhere[^1].

[^1]: Also see [Ubiquitous computing](https://en.wikipedia.org/wiki/Ubiquitous_computing), 

This first stage experiments with some infrastructure technologies and ideas.

Our main goal is to progressively hook together a queryable, shared world-model, a camera, a projector, and small programs to achieve a [projector-camera system](https://en.wikipedia.org/wiki/Projector_camera_systems)[^2].

[^2]: For a review and further discussion, see eg [UbiBeam: Exploring the Interaction Space for Home Deployed Projector-Camera Systems](https://www.researchgate.net/publication/285333899_UbiBeam_Exploring_the_Interaction_Space_for_Home_Deployed_Projector-Camera_Systems)

Our choices for this iteration:

- **System architecture**: small, independent, and loosely-coupled peers communating via a shared workspace; following ideas from P2P architectures and [Microkernels](https://en.wikipedia.org/wiki/Microkernel)

- **Programming paradigm**: inspired by [Production Rules](https://en.wikipedia.org/wiki/Production_system_(computer_science)), [Blackboard Systems](https://en.wikipedia.org/wiki/Blackboard_system), [Tuple Spaces](https://en.wikipedia.org/wiki/Tuple_space), and [Reactive programming](https://en.wikipedia.org/wiki/Reactive_programming)

- **Modelling the interactive space**: via [O'Doyle Rules](https://github.com/oakes/odoyle-rules)

- **Communication technology**: Websockets, with p2p semantics

- **Host language(s)**: Clojure

This is the first brick in the wall. It is not yet a house. It is not even a room. But it is a lot of fun. 


## License

Copyright © 2024 Barbican Inference

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
