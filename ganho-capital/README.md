---
owner: "#chapter-technical-writing"
---

# README Template
- Please take a moment to write this document before putting the service into `prod`. Remember: a well-written README provides a better understanding of the service for everyone.

- As an example, refer to this [README](https://github.com/nubank/buffet#readme)

- If you need any help or have suggestions to improve it, please reach out to us in [#guild-eng-docs](https://nubank.slack.com/archives/C7QFVNW8Y) in Slack.

- Erase this message (lines 5-15) before publishing the file.

----

# What is it?
A categorical description of the service, for example: “ganho-capital is a logging service...”.

# What problem(s) does it strive to solve?
This is the most important part of the document! We need to build and adopt tech that solves our problems. Good tech is clearly pointed at solving one or a few problems. When summarizing a tech that you did not build, try to put yourself in the shoes of those who did. What was their mission, what problem(s) were they taking on, and how did they intend to distinguish their solution from others? Try to avoid this being a list of features without connecting those features to problems.

# Core abstractions
What is it about? A good tech has a few clearly defined abstractions around which you can build an understanding of its facilities.

# Primary operations
Similarly, good tech has a few well-defined operations that can be composed into solutions.

# Architectural Components
Are there multiple processes, services or components involved, and how do they fit together? Are there environmental (AWS) or supportive (Kafka, Zookeeper) requirements? Diagrams are welcome here.

# Is it simple?
In the ["Simple Made Easy"](https://www.youtube.com/watch?v=LKtk3HCgTa8) sense: is the tech complex? Is it stateful? Does it have many interdependencies with other services or libraries?

# Fundamental tradeoffs
Every tech has benefits but no tech is free of tradeoffs. We need to be explicit about tradeoffs in order to make good decisions, and to prepare compensations. Tradeoffs should be expressed in terms of “gives up X to get Y”, and not just a list of negatives.

## Benefits

## Risks

# Unknowns
After having done the research to prepare this summary, what are the key aspects that are unknown, e.g. performance characteristics under load etc.

# Key indicators for use
In what circumstances is this service a good fit and why?

# Key indicators against use
In what circumstances is this service a poor fit and why?

# Alternatives
Are there alternative services in the same space? In what ways do they differ, e.g. by taking on a slightly different problem set, emphasizing different aspects, or making different tradeoffs?

# Other relevant characteristics
Are there operational (latency, scalability, security) aspects which should be highlighted?

# Resources
Please include at links to any high level overviews, design documents, or presentations so people can dive deeper.

# Development Practices and Environmental Setup

## Code Style

* Check our [Clojure Code Style Best Practices Recommendations](https://github.com/nubank/playbooks/blob/master/development/backend/clojure/code-style.md)

## Linting

### List issues

```
lein lint
```

### Fix issues

```
lein lint-fix
```

## Testing

Run all tests:
```bash
$ lein test
```

Run autotest (make sure you have `[com.jakemccrary/lein-test-refresh "0.24.1"]` set as a lein plugin)

```bash
$ lein test-refresh :changes-only
```

### Unit tests

```
$ lein unit
```

### Integration tests

```
$ lein integration
```
# Etymology
Why was this service name chosen? How does the chosen name help users remember what it is that the service does?
* When creating a new service, it is best practice to ask for advice in the [#service-names channel](https://nubank.slack.com/app_redirect?channel=service-names).
* Before choosing a name that includes an allusion, think carefully about whether the name will help users remember what it is that the service does.
* If the name is an allusion, (perhaps for consistency with related existing services whose names share a theme), please include a link to a source where readers can learn more about the reference. You may also consider including an image of the namesake.
