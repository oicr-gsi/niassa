[![Join the chat at https://gitter.im/oicr-gsi-niassa](https://badges.gitter.im/oicr-gsi-niassa.svg)](https://gitter.im/oicr-gsi-niassa?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Niassa is a bioinformatics workflow engine and analysis provenance system. The
different components of the project allow users to create analysis workflows,
string workflows together in pipelines, run that analysis either locally or in
the cloud, and finally retrieve information about what analysis was run, with
what parameters, and where the resulting files are located.

This README is a quick overview of the Niassa codebase. See our
[project homepage](https://oicr-gsi.github.io/niassa-docs/) for much more documentation
on installation and usage.

### Note

Niassa is a fork and extension of [SeqWare](http://seqware.io/) so many parts of
the system continue to be named SeqWare or `seqware`. Don’t panic; this is
intentional and pending future updates.

## Modules

The Niassa modules included in this repository are:

Core modules:

* seqware-common : core functions, including DAOs. Most other projects depend on this one.
* [seqware-meta-db](seqware-meta-db) : SQL and other things for the [MetaDB]()
* [seqware-pipeline](seqware-pipeline) : workflow engines and workflow API code
* [seqware-webservice](seqware-webservice) : RESTful API endpoints for Pipeline and MetaDB functions
* seqware-distribution : builds all other modules into the single seqware-distribution JAR

Other modules:

* seqware-archetypes : Maven archtypes for workflows and deciders
* seqware-ext-testing : integration and system tests
* seqware-sanity-check : a tool to check whether Niassa is installed and configured properly

## Building

See [Niassa Build Guide](https://oicr-gsi.github.io/niassa-docs/current/installation/building)

## Installing

See [Niassa Installation Guide](https://oicr-gsi.github.io/niassa-docs/current/installation).

## Contributors

Please see our [partners and contributors](https://github.com/oicr-gsi/niassa/graphs/contributors)

## License

Niassa is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Niassa is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the [GNU General Public License](LICENSE.txt)
along with Niassa.  If not, see <http://www.gnu.org/licenses/>.

## Contact

Niassa is built and maintained by the
[Genome Sequence Informatics](https://gsi.oicr.on.ca) group at
[Ontario Institute for Cancer Research](https://oicr.on.ca). Get in touch by
submitting a question or issue to our
[Github issue tracker](https://github.com/oicr-gsi/niassa/issues).
