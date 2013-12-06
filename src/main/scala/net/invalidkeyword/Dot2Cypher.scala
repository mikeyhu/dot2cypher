package net.invalidkeyword

/**
 * Created with IntelliJ IDEA.
 * User: mikeyhu
 * Date: 22/11/2013
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */

import uk.co.turingatemyhamster.graphvizs._
import uk.co.turingatemyhamster.graphvizs.dsl._
import java.io.{InputStreamReader, FileInputStream, Reader}
import uk.co.turingatemyhamster.graphvizs.dsl.ID.Identifier
import uk.co.turingatemyhamster.graphvizs.dsl.Node
import uk.co.turingatemyhamster.graphvizs.dsl.StatementType.Node
import uk.co.turingatemyhamster.graphvizs.dsl.Node

case class CreateNode(nodeName:String) {
  val variableName = nodeName.toLowerCase().replaceAll(" ","")
  override def toString = s"CREATE (${variableName}:Class {name:'${nodeName}'})"
}

case class CreateRelationship(nodeFrom:CreateNode, relationship:String, nodeTo:CreateNode) {
  override def toString = s"CREATE (${nodeFrom.variableName}})-[:${relationship}]->(${nodeTo.variableName}})"
}


object Dot2Cypher {


  def main(args:Array[String]) = {


    convertToCypher(parseGraph())

  }

  def parseGraph() : Graph = {
    val reader = new InputStreamReader(new FileInputStream("/Users/mikeyhu/tmp/code-graphs/core-2011.dot"))

    val parser = new DotAstParser
    import parser._
    val result : ParseResult[Graph] = parser.parseAll(graph,reader)

    result.get
  }

  def convertToCypher(element: Graph) = {


    element.statements.foreach{
      case NodeStatement(nodeId,attributes) => println("Node : " + displayNode(nodeId))
      //case EdgeStatement(nodeFrom,nodes,attributes) => println("Edge : " + displayRelationship(nodeFrom.asInstanceOf[NodeStatement],nodes.head))
      case EdgeStatement(nodeFrom,nodesTo,_) => nodesTo.foreach { nodeTo => println(displayRelationship(nodeFrom.asInstanceOf[NodeId],nodeTo._2.asInstanceOf[NodeId]))}
      case other => println("Unknown : " + other)
    }

  }

  def displayNode(node : NodeId) = {
    CreateNode(node.id)
  }

  def displayRelationship(nodeFrom:NodeId, nodeTo:NodeId) = {
    CreateRelationship(CreateNode(nodeFrom.id),"EXTENDS",CreateNode(nodeTo.id))
  }

  implicit def IDtoString(id : ID) : String = {
    id match {
      case Identifier(value) => value
    }
  }

  implicit def NodetoString(node : NodeStatement) : String = {
    node match {
      case NodeStatement(nodeId,_) => nodeId.id
    }
  }

}
