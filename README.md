# DRES Client Examples

This is a service repository with examples on how to use the 
[Official DRES Client OAS](https://github.com/dres-dev/DRES/blob/master/doc/oas-client.json)
to create participating clients for [DRES](https://github.com/dres-dev/DRES/).

Currently, there are examples in these languages:

* [Java](java/README.md)
* [Kotlin](kotlin/README.md)
* [c#](csharp/README.md)
* [typescript/ angular](angular-ts/README.md)

## Prerequisites

To have the examples somewhat aligned, we rely on the great
[Gradle OpenAPI Generator Plugin](https://github.com/OpenAPITools/openapi-generator/tree/master/modules/openapi-generator-gradle-plugin)
and thus, on [Gradle](https://gradle.org) - however, as we use the gradle wrapper, you shouldn't notice this.

Besides that, the examples are in various languages with their own set of prerequisites.

## Citation

We kindly ask you to refer to the following paper in publications mentioning or employing DRES:

> Rossetto L., Gasser R., Sauter L., Bernstein A., Schuldt H. (2021) A System for Interactive Multimedia Retrieval Evaluations. In: Lokoč J. et al. (eds) MultiMedia Modeling. MMM 2021. Lecture Notes in Computer Science, vol 12573. Springer, Cham.
**Link:** https://doi.org/10.1007/978-3-030-67835-7_33


**Bibtex:**
```
@InProceedings{10.1007/978-3-030-67835-7_33,
author="Rossetto, Luca
and Gasser, Ralph
and Sauter, Loris
and Bernstein, Abraham
and Schuldt, Heiko",
editor="Loko{\v{c}}, Jakub
and Skopal, Tom{\'a}{\v{s}}
and Schoeffmann, Klaus
and Mezaris, Vasileios
and Li, Xirong
and Vrochidis, Stefanos
and Patras, Ioannis",
title="A System for Interactive Multimedia Retrieval Evaluations",
booktitle="MultiMedia Modeling",
year="2021",
publisher="Springer International Publishing",
address="Cham",
pages="385--390",
abstract="The evaluation of the performance of interactive multimedia retrieval systems is a methodologically non-trivial endeavour and requires specialized infrastructure. Current evaluation campaigns have so far relied on a local setting, where all retrieval systems needed to be evaluated at the same physical location at the same time. This constraint does not only complicate the organization and coordination but also limits the number of systems which can reasonably be evaluated within a set time frame. Travel restrictions might further limit the possibility for such evaluations. To address these problems, evaluations need to be conducted in a (geographically) distributed setting, which was so far not possible due to the lack of supporting infrastructure. In this paper, we present the Distributed Retrieval Evaluation Server (DRES), an open-source evaluation system to facilitate evaluation campaigns for interactive multimedia retrieval systems in both traditional on-site as well as fully distributed settings which has already proven effective in a competitive evaluation.",
isbn="978-3-030-67835-7"
}
```

## Contributing

Contributions are always welcome. Feel free to add the example in your preferred language and create a PR for it.

