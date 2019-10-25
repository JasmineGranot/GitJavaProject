function forkRepository(repoId) {
    var forkRepo = repoId;
    console.log(forkRepo);
    $.ajax ({
        url:"../fork",
        data:
            {
                repositoryName: forkRepo
            },
    });
}

