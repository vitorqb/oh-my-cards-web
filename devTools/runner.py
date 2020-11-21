#!/bin/env python3
import argparse
import subprocess
import os
import logging
import multiprocessing


logging.basicConfig(level=logging.INFO)


# Helpers
class Logger:

    def __init__(self, c, name):
        self._c = c
        self._name = name
        self._logger = self.initLogging()

    def initLogging(self):
        path = f"{self._c.cwd}/devTools/logs/{self._name}.log"
        file_handler = logging.FileHandler(path)
        logger = logging.getLogger(self._name)
        logger.addHandler(file_handler)
        return logger

    def info(self, x):
        self._logger.info(x)


class Context:

    def __init__(self, args):
        self.cwd = args.root_dir

    def run(self, name, cmd):
        logging.info(f"Running {name} with cmd {cmd} in cwd {self.cwd}")
        logger = Logger(self, name)
        process = subprocess.Popen(
            cmd,
            cwd=self.cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT
        )
        for c in iter(process.stdout.readline, b''):
            logger.info(c.decode().rstrip())

    def run_async(self, name, cmd):
        return multiprocessing.Process(target=self.run, args=(name, cmd))


# Arg parsing
parser = argparse.ArgumentParser(
    description="OhMyCards web development runner"
)
parser.add_argument(
    "--root-dir",
    help="The root directory",
    default=os.environ['OHMYCARDS_WEB_ROOT_DIR']
)


# Main
def run(c, args):
    tasks = [
        c.run_async("CompileCljs", ["make", "watch"]),
        c.run_async("RunBackend", ["make", "runBackend"]),
        c.run_async("CompileScsss", ["make", "scss"]),
        c.run_async("RevProxy",  ["make", "rev-proxy"])
    ]
    for x in tasks:
        x.start()
    for x in tasks:
        x.join()


# Main
if __name__ == "__main__":
    args = parser.parse_args()
    context = Context(args)
    run(context, args)
